package com.example.midproject_imdb.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.midproject_imdb.data.models.MovieTMDB

@Database(entities = [MovieTMDB::class], version = 2, exportSchema = false)
abstract class MovieTMDBDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieTMDBDao

    companion object {
        @Volatile
        private var INSTANCE: MovieTMDBDatabase? = null

        fun getDatabase(context: Context): MovieTMDBDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieTMDBDatabase::class.java,
                    "movie_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}