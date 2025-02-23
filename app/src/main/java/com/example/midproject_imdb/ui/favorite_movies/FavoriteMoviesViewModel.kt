package com.example.midproject_imdb.ui.favorite_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.data.repositories.MovieTMDbRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class FavoriteMoviesViewModel @Inject constructor(
    private val repository: MovieTMDbRepo
) : ViewModel() {

    private val _favoriteMovies = MutableLiveData<List<MovieTMDB>>()
    val favoriteMovies: LiveData<List<MovieTMDB>> = _favoriteMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        _isLoading.value = true
        repository.getAllFavoriteMovies()
            .onEach { movies ->
                _favoriteMovies.value = movies
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = "Error loading favorites: ${e.message}"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun removeFromFavorites(movie: MovieTMDB) {
        viewModelScope.launch {
            try {
                repository.removeFromFavorites(movie)
            } catch (e: Exception) {
                _error.value = "Error removing from favorites: ${e.message}"
            }
        }
    }
}
