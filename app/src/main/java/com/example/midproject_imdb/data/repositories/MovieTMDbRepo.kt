package com.example.midproject_imdb.data.repositories

import com.example.midproject_imdb.data.local_db.MovieTMDBDao
import com.example.midproject_imdb.data.models.MovieTMDB
import com.example.midproject_imdb.data.retrofit.MovieApiService
import com.example.midproject_imdb.data.retrofit.MovieResponse
import com.example.midproject_imdb.data.retrofit.MovieSearchResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MovieTMDbRepo  @Inject constructor(
    private val movieDao: MovieTMDBDao,
    private val movieApiService: MovieApiService
) {
    fun getAllFavoriteMovies(): Flow<List<MovieTMDB>> = movieDao.getAllFavoriteMovies()

    fun isMovieFavorite(movieId: Int): Flow<Boolean> = movieDao.isMovieFavorite(movieId)

    suspend fun addToFavorites(movie: MovieTMDB) = movieDao.insertMovie(movie.copy(isFavorite = true))

    suspend fun removeFromFavorites(movie: MovieTMDB) = movieDao.deleteMovie(movie.copy(isFavorite = false))

    suspend fun searchMovies(query: String): List<MovieTMDB> {
        val response = movieApiService.searchMovies(query)
        return response.results.map { it.toMovie() }
    }

    suspend fun getNearbyMovies(region: String): MovieSearchResponse {
        return movieApiService.getNearbyMovies(region)
    }

    private fun MovieResponse.toMovie() = MovieTMDB(
        id = id,
        title = title,
        overview = overview,
        posterPath = poster_path,
        releaseDate = release_date,
        voteAverage = vote_average,
        isFavorite = false
    )
}
