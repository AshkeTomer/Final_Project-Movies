package com.example.midproject_imdb.ui.movie_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.data.repositories.MovieTMDbRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel  @Inject constructor(
    private val repository: MovieTMDbRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movies = savedStateHandle.getLiveData<List<MovieTMDB>>("movies", emptyList())
    val movies: LiveData<List<MovieTMDB>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentQuery = savedStateHandle.getLiveData<String>("current_query", "")
    val currentQuery: LiveData<String> = _currentQuery

    private val _isNetworkError = savedStateHandle.getLiveData<Boolean>("isNetworkError", false)
    val isNetworkError: LiveData<Boolean> = _isNetworkError


    init {
        // Restore search results if there was a previous query
        _currentQuery.value?.takeIf { it.isNotEmpty() }?.let { query ->
            searchMovies(query)
        }
    }

    fun searchMovies(query: String) {
        savedStateHandle["current_query"] = query
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val searchResults = repository.searchMovies(query)
                updateMoviesWithFavoriteState(searchResults)
                _isNetworkError.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                _isNetworkError.value = true
                _isLoading.value = false
            }
        }
    }

    private suspend fun updateMoviesWithFavoriteState(movies: List<MovieTMDB>) {
        val updatedResults = movies.map { movie ->
            val isFavorite = repository.isMovieFavorite(movie.id).firstOrNull() ?: false
            movie.copy(isFavorite = isFavorite)
        }
        _movies.value = updatedResults
    }

    fun toggleFavorite(movie: MovieTMDB) {
        viewModelScope.launch {
            try {
                val isFavorite = repository.isMovieFavorite(movie.id).firstOrNull() ?: false
                // Update UI immediately for better responsiveness
                _movies.value = _movies.value?.map {
                    if (it.id == movie.id) it.copy(isFavorite = !isFavorite) else it
                }

                // Then update the database
                if (isFavorite) {
                    repository.removeFromFavorites(movie)
                } else {
                    repository.addToFavorites(movie)
                }
            } catch (e: Exception) {
                _error.value = "Error updating favorite: ${e.message}"
                // Revert UI state in case of error
                _movies.value = _movies.value?.map {
                    if (it.id == movie.id) it.copy(isFavorite = !it.isFavorite) else it
                }
            }
        }
    }
}