package com.example.midproject_imdb.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.midproject_imdb.data.local_db.MovieDao
import com.example.midproject_imdb.data.local_db.MovieDataBase
import com.example.midproject_imdb.data.models.Movie

class MovieRepository(application: Application) {

    private var movieDao: MovieDao?

    init {
        val db  = MovieDataBase.getDatabase(application.applicationContext)
        movieDao = db.moviesDao()
    }

    fun getMovies() = movieDao?.getMovies()

    suspend fun addMovie(movie: Movie) {
        movieDao?.addMovie(movie)
    }
    val allMovies: LiveData<List<Movie>>? = movieDao?.getAllMovies()

    suspend fun updateMovie(movie: Movie) {
        movieDao?.updateMovie(movie)
    }
    suspend fun deleteMovie(movie: Movie) {
        movieDao?.deleteMovie(movie)
    }

    suspend fun insertMovies(movies: List<Movie>) {
        movieDao?.insertMovies(movies)
    }
}