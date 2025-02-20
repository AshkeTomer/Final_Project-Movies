
package com.example.midproject_imdb.ui.MovieTMDBDetailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.MovieTmdbDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieTMDBDetailFragment : Fragment() {
    private var _binding: MovieTmdbDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieTMDBDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieTmdbDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            viewModel.setMovieDetails(
                title = args.getString("title", "Unknown Title"),
                overview = args.getString("overview", "No overview available"),
                rating = args.getFloat("rating", 0f),
                releaseDate = args.getString("releaseDate"),
                posterPath = args.getString("posterPath")
            )
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.movieTitle.observe(viewLifecycleOwner) { title ->
            binding.movieTitle.text = title
        }

        viewModel.movieOverview.observe(viewLifecycleOwner) { overview ->
            binding.movieOverview.text = overview
        }

        viewModel.movieRating.observe(viewLifecycleOwner) { rating ->
            binding.movieRating.text = String.format("%.1f", rating)
        }

        viewModel.movieReleaseDate.observe(viewLifecycleOwner) { releaseDate ->
            binding.movieReleaseDate.text = releaseDate ?: getString(R.string.release_date_not_available)
        }

        viewModel.moviePosterPath.observe(viewLifecycleOwner) { posterPath ->
            posterPath?.takeIf { it.isNotEmpty() }?.let { path ->
                val fullImageUrl = viewModel.getFullPosterUrl(path)
                // Load image into both views
                Glide.with(requireContext())
                    .load(fullImageUrl)
                    .into(binding.moviePoster)

                Glide.with(requireContext())
                    .load(fullImageUrl)
                    .into(binding.movieBackdrop)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}