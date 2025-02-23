package com.example.midproject_imdb.ui.movie_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.MovieSearchBinding
import com.example.midproject_imdb.ui.favorite_movies.MovieTMDBAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieSearchFragment : Fragment() {

    private var _binding: MovieSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieSearchViewModel by viewModels()

    private lateinit var movieAdapter: MovieTMDBAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupNavigation()
        observeViewModel()
    }

    private fun setupNavigation() {
        binding.favoritesButton.setOnClickListener {
            findNavController().navigate(R.id.action_search_to_favorites)
        }
        binding.nearbyButton.setOnClickListener {
            findNavController().navigate(R.id.action_search_to_nearby)
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieTMDBAdapter(
            onFavoriteClick = { movie ->
                viewModel.toggleFavorite(movie)
            },
            isFavorite = { movieId ->
                viewModel.movies.value?.find { it.id == movieId }?.isFavorite ?: false
            }
        )
        binding.moviesRecyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        // Restore previous search query
        viewModel.currentQuery.value?.let { query ->
            binding.searchView.setQuery(query, false)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        viewModel.searchMovies(it.trim())
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun observeViewModel() {
        // Observe movies
        viewModel.movies.observe(viewLifecycleOwner) { movies ->

            movieAdapter.setMovies(movies)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.moviesRecyclerView.visibility = View.GONE
                binding.errorContainer.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.moviesRecyclerView.visibility = View.GONE
                binding.errorContainer.visibility = View.VISIBLE
                binding.errorMessage.text = getString(R.string.no_internet_connection)
                binding.retryButton.setOnClickListener {

                    binding.searchView.query?.toString()?.let { query ->
                        if (query.isNotBlank()) {
                            viewModel.searchMovies(query.trim())
                        }
                    }
                }
            }
        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                binding.moviesRecyclerView.visibility = View.GONE
                binding.errorContainer.visibility = View.VISIBLE
                binding.retryButton.setOnClickListener {
                    binding.searchView.query?.toString()?.let { query ->
                        if (query.isNotBlank()) {
                            viewModel.searchMovies(query.trim())
                        }
                    }
                }
            } else {
                binding.moviesRecyclerView.visibility = View.VISIBLE
                binding.errorContainer.visibility = View.GONE
            }
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}