package com.example.midproject_imdb.di
import com.example.midproject_imdb.data.local_db.MovieDao

import android.content.Context
import com.example.midproject_imdb.data.local_db.MovieDataBase
import com.example.midproject_imdb.data.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object appmodule {


    @Provides
    @Singleton
    fun provideContext(application: android.app.Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun provideMovieDatabase(context: Context): MovieDataBase {
        return MovieDataBase.getDatabase(context)
    }


    @Provides
    @Singleton
    fun provideMovieDao(movieDatabase: MovieDataBase) = movieDatabase.moviesDao()


    @Provides
    @Singleton
    fun provideMovieRepository(movieDao: MovieDao) = MovieRepository(movieDao)
}
