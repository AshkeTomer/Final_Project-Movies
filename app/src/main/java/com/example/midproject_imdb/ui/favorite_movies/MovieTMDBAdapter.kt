package com.example.midproject_imdb.ui.favorite_movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.MovieApiItemBinding
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.ui.detail_movie.MovieTMDBDetailFragment

class MovieTMDBAdapter(
    private val onFavoriteClick: (MovieTMDB) -> Unit,
    private val isFavorite: (Int) -> Boolean,

) : RecyclerView.Adapter<MovieTMDBAdapter.MovieViewHolder>() {

    private var movies: List<MovieTMDB> = emptyList()

    inner class MovieViewHolder(private val binding: MovieApiItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieTMDB) {
            // Set movie title
            binding.movieTitle.text = movie.title

            // Set movie overview
            binding.movieOverview.text = movie.overview

            // Set movie rating
            binding.movieRating.text = String.format("%.1f", movie.voteAverage)

            // Set release date
            binding.movieReleaseDate.text = movie.releaseDate

            // Load movie poster
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(binding.root)
                .load(imageUrl)
//                .circleCrop()
                .into(binding.moviePoster)

            // Clear any existing listeners to prevent duplicates
            binding.favoriteButton.setOnClickListener(null)

            // Set click listener for the whole item
            binding.root.setOnClickListener {
                showMovieDetails(movie)
            }

            // Set click listener with animation
            binding.favoriteButton.setOnClickListener { view ->
                view.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start()
                        onFavoriteClick(movie)
                    }
                    .start()
            }
        }
        private fun showMovieDetails(movie: MovieTMDB) {
            try {
                val bundle = bundleOf(
                    "title" to movie.title,
                    "overview" to movie.overview,
                    "rating" to movie.voteAverage.toFloat(),
                    "releaseDate" to (movie.releaseDate ?: ""),
                    "posterPath" to (movie.posterPath ?: "")
                )

                val navController = Navigation.findNavController(binding.root)

                // Determine which fragment we're in and use appropriate action
                when (navController.currentDestination?.id) {
                    R.id.movieSearchFragment -> {
                        navController.navigate(R.id.action_search_to_detail, bundle)
                    }
                    R.id.favoriteMoviesFragment -> {
                        navController.navigate(R.id.action_favorites_to_detail, bundle)
                    }
                    R.id.nearbyMoviesFragment -> {
                        navController.navigate(R.id.action_nearby_to_detail, bundle)
                    }

                }
            } catch (e: Exception) {
                Log.e("MovieTMDBAdapter", "Navigation error: ${e.message}")
            }
        }
    }

    fun setMovies(newMovies: List<MovieTMDB>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    fun getMovieAt(position: Int) = movies[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieViewHolder(MovieApiItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bind(movies[position])

    override fun getItemCount() = movies.size
}