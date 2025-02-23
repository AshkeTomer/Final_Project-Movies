package com.example.midproject_imdb.ui.favorite_movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midproject_imdb.R
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.databinding.FavoriteMoviesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteMoviesFragment : Fragment() {

    private var _binding: FavoriteMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteMoviesViewModel by viewModels()

    private lateinit var movieAdapter: MovieTMDBAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupNavigation()
        observeViewModel()
    }

    private fun setupNavigation() {
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_favorites_to_search)
        }
        binding.nearbyButton.setOnClickListener {
            findNavController().navigate(R.id.action_favorites_to_nearby)
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieTMDBAdapter(
            onFavoriteClick = { movie ->
                viewModel.removeFromFavorites(movie)
            },
            isFavorite = { _ -> true }

        )
        binding.favoritesRecyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies: List<MovieTMDB> ->
            movieAdapter.setMovies(movies)
            binding.emptyStateText.isVisible = movies.isEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error: String? ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}