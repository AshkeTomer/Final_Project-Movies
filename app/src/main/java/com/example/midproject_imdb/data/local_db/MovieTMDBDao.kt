package com.example.midproject_imdb.data.local_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.midproject_imdb.data.models.MovieTMDB
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieTMDBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieTMDB)

    @Delete
    suspend fun deleteMovie(movie: MovieTMDB)

    @Query("SELECT * FROM favorite_movies")
    fun getAllFavoriteMovies(): Flow<List<MovieTMDB>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
}