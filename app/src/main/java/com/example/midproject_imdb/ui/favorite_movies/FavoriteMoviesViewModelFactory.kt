package com.example.midproject_imdb.ui.favorite_movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.midproject_imdb.data.repositories.MovieRepo

class FavoriteMoviesViewModelFactory(
    private val repository: MovieRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteMoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteMoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}