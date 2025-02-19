package com.example.midproject_imdb.core

import android.app.Application
import com.example.midproject_imdb.data.local_db.MovieTMDBDatabase
import com.example.midproject_imdb.data.network.NetworkModule
import com.example.midproject_imdb.data.repositories.MovieRepo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApplication : Application() {

    val database by lazy { MovieTMDBDatabase.getDatabase(this) }
    val repository by lazy {
        MovieRepo(
            movieDao = database.movieDao(),
            movieApiService = NetworkModule.movieApiService
        )
    }

    companion object {
        private var instance: MovieApplication? = null

        fun getInstance(): MovieApplication {
            return instance ?: throw IllegalStateException("MovieApplication has not been initialized")
        }
    }

    override fun onCreate() {
        super.onCreate()
        // This ensures that instance is only set once
        if (instance == null) {
            instance = this
        }
    }
}
