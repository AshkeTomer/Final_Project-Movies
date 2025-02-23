package com.example.midproject_imdb.ui.nearby_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.data.repositories.MovieTMDbRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class NearbyMoviesViewModel @Inject constructor(private val MovieTMDbRepo: MovieTMDbRepo) : ViewModel() {
    private val _movies = MutableLiveData<List<MovieTMDB>>()
    val movies: LiveData<List<MovieTMDB>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _favoriteStatusUpdated = MutableLiveData<Pair<Int, Boolean>>()
    val favoriteStatusUpdated: LiveData<Pair<Int, Boolean>> = _favoriteStatusUpdated

    // Location state management
    private val _locationState = MutableLiveData<LocationState>()
    val locationState: LiveData<LocationState> = _locationState

    fun getFavoriteStatus(movieId: Int): Boolean {
        var isFavorite = false
        _movies.value?.find { it.id == movieId }?.let { movie ->
            isFavorite = movie.isFavorite
        }
        return isFavorite
    }

  // Updates the favorite status of a movie in the repository and updates the UI accordingly.
    private fun checkAndUpdateFavoriteStatus(movieId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = MovieTMDbRepo.isMovieFavorite(movieId).firstOrNull() ?: false
                _favoriteStatusUpdated.value = movieId to isFavorite
                updateMovieList(movieId, isFavorite)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    // Updates the location state and triggers appropriate actions based on the new state.
    fun setLocationState(state: LocationState) {
        _locationState.value = state
        when (state) {
            is LocationState.LocationReady -> fetchNearbyMovies(state.countryCode)
            is LocationState.Error -> _error.value = state.message
            else -> _movies.value = emptyList()
        }
    }
//Fetches nearby movies based on the provided region code.
    private fun fetchNearbyMovies(region: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = MovieTMDbRepo.getNearbyMovies(region)
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
                movies.forEach { movie -> checkAndUpdateFavoriteStatus(movie.id) }
            } catch (e: Exception) {
                _error.value = e.message
                _movies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
   //Adds a movie to favorites in the repository and updates the UI
    fun addToFavorites(movie: MovieTMDB) {
        viewModelScope.launch {
            try {
                MovieTMDbRepo.addToFavorites(movie.copy(isFavorite = true))
                updateMovieList(movie.id, true)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
// Removes a movie from favorites in the repository and updates the UI.
    fun removeFromFavorites(movie: MovieTMDB) {
        viewModelScope.launch {
            try {
                MovieTMDbRepo.removeFromFavorites(movie)
                updateMovieList(movie.id, false)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
// Updates the favorite status of a movie in the current movie list
    private fun updateMovieList(movieId: Int, isFavorite: Boolean) {
        _movies.value = _movies.value?.map { movie ->
            if (movie.id == movieId) movie.copy(isFavorite = isFavorite) else movie
        }
    }


}

