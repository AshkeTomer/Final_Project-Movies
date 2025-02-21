    package com.example.midproject_imdb.data.local_db

    import androidx.lifecycle.LiveData
    import androidx.room.Dao
    import androidx.room.Delete
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import androidx.room.Update
    import com.example.midproject_imdb.data.models.Movie

    @Dao
    interface MovieDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun addMovie(movie: Movie)



        @Query("SELECT * FROM movies")
        fun getAllMovies(): LiveData<List<Movie>>

        @Delete
        suspend fun deleteMovie(vararg  movie: Movie)

        @Update
        suspend fun updateMovie(movie: Movie)

        @Query("SELECT * FROM movies ORDER BY title ASC")
        fun getMoviesByAscOrder(): LiveData<List<Movie>>

        @Query("SELECT * FROM movies WHERE id =:id")
         fun getMovie(id: Int): LiveData<Movie>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertMovies(movies: List<Movie>)
    }