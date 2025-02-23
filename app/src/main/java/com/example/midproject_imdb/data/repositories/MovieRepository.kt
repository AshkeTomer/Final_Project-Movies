package com.example.midproject_imdb.data.repositories

import androidx.lifecycle.LiveData
import com.example.midproject_imdb.data.local_db.MovieDao
import com.example.midproject_imdb.data.models.Movie
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao
)
{

    fun getMoviesByAscOrder() = movieDao.getMoviesByAscOrder()

    suspend fun addMovie(movie: Movie) {
        movieDao.addMovie(movie)
    }
    val allMovies: LiveData<List<Movie>> = movieDao.getAllMovies()

    suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }
    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun insertMovies(movies: List<Movie>) {
        movieDao.insertMovies(movies)
    }
}