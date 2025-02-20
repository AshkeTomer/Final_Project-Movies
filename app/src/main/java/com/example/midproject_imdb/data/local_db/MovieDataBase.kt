package com.example.midproject_imdb.data.local_db

import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import com.example.midproject_imdb.data.models.Movie

@Database(entities = arrayOf(Movie::class), version = 3, exportSchema = false)
abstract class MovieDataBase : RoomDatabase() {

    abstract fun moviesDao() : MovieDao

    companion object {

        @Volatile
        private var instance: MovieDataBase? = null

        fun getDatabase(context: Context) =

             instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDataBase::class.java,
                    "movies_db")
                    .build()
            }

    }
}