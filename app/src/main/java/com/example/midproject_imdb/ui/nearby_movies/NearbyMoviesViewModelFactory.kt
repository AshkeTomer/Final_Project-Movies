package com.example.midproject_imdb.ui.nearby_movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.midproject_imdb.data.repositories.MovieRepo

class NearbyMoviesViewModelFactory(private val movieRepo: MovieRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NearbyMoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NearbyMoviesViewModel(movieRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
