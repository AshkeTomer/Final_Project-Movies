
package com.example.midproject_imdb.ui.detail_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.MovieTmdbDetailBinding
import com.example.midproject_imdb.data.models.MovieTMDB


class MovieTMDBDetailFragment : Fragment() {
    private var _binding: MovieTmdbDetailBinding? = null
    private val binding get() = _binding!!

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
            binding.apply {
                movieTitle.text = args.getString("title", "Unknown Title")
                movieOverview.text = args.getString("overview", "No overview available")
                movieRating.text = String.format("%.1f", args.getFloat(getString(R.string.rating), 0f))
                movieReleaseDate.text = args.getString("releaseDate") ?: "Release date not available"

                args.getString("posterPath")?.takeIf { it.isNotEmpty() }?.let { path ->
                    val fullImageUrl = "https://image.tmdb.org/t/p/w500$path"
                    // Load image into both views
                    Glide.with(requireContext())
                        .load(fullImageUrl)
                        .into(moviePoster)


                        Glide.with(requireContext())
                            .load(fullImageUrl)
                            .into(movieBackdrop)

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}