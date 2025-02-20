package com.example.midproject_imdb.ui.nearby_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.data.repositories.MovieRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class NearbyMoviesViewModel @Inject constructor (private val movieRepo: MovieRepo) : ViewModel() {
    private val _movies = MutableLiveData<List<MovieTMDB>>()
    val movies: LiveData<List<MovieTMDB>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _favoriteStatusUpdated = MutableLiveData<Pair<Int, Boolean>>()
    val favoriteStatusUpdated: LiveData<Pair<Int, Boolean>> = _favoriteStatusUpdated

    fun getFavoriteStatus(movieId: Int): Boolean {
        var isFavorite = false
        _movies.value?.find { it.id == movieId }?.let { movie ->
            isFavorite = movie.isFavorite
        }
        return isFavorite
    }

    private fun checkAndUpdateFavoriteStatus(movieId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = movieRepo.isMovieFavorite(movieId).firstOrNull() ?: false
                _favoriteStatusUpdated.value = movieId to isFavorite
                updateMovieList(movieId, isFavorite)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchNearbyMovies(region: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = movieRepo.getNearbyMovies(region)
                val movies = response.results.map { movieResponse ->
                    MovieTMDB(
                        id = movieResponse.id,
                        title = movieResponse.title,
                        overview = movieResponse.overview,
                        posterPath = movieResponse.poster_path,
                        releaseDate = movieResponse.release_date,
                        voteAverage = movieResponse.vote_average,
                        isFavorite = false
                    )
                }
                _movies.value = movies
                // Check favorite status for each movie
                movies.forEach { movie ->
                    checkAndUpdateFavoriteStatus(movie.id)
                }
            } catch (e: Exception) {
                // Handle error
                _movies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorites(movie: MovieTMDB) {
        viewModelScope.launch {
            movieRepo.addToFavorites(movie.copy(isFavorite = true))
            updateMovieList(movie.id, true)
        }
    }

    fun removeFromFavorites(movie: MovieTMDB) {
        viewModelScope.launch {
            movieRepo.removeFromFavorites(movie)
            updateMovieList(movie.id, false)
        }
    }

    private fun updateMovieList(movieId: Int, isFavorite: Boolean) {
        _movies.value = _movies.value?.map { movie ->
            if (movie.id == movieId) movie.copy(isFavorite = isFavorite) else movie
        }
    }

    fun isMovieFavorite(movieId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val isFavorite = movieRepo.isMovieFavorite(movieId).firstOrNull() ?: false
                callback(isFavorite)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }
}
