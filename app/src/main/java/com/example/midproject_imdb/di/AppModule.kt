package com.example.midproject_imdb.di
import android.app.Application
import com.example.midproject_imdb.data.local_db.MovieDao

import android.content.Context
import com.example.midproject_imdb.data.local_db.MovieDataBase
import com.example.midproject_imdb.data.local_db.MovieTMDBDao
import com.example.midproject_imdb.data.local_db.MovieTMDBDatabase
import com.example.midproject_imdb.data.retrofit.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {






    // Database Dependencies
    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDataBase {
        return MovieDataBase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMovieDao(movieDatabase: MovieDataBase): MovieDao {
        return movieDatabase.moviesDao()
    }

    @Provides
    @Singleton
    fun provideTMDBDatabase(@ApplicationContext context: Context): MovieTMDBDatabase {
        return MovieTMDBDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTMDBDao(database: MovieTMDBDatabase): MovieTMDBDao {
        return database.movieDao()
    }

    // Network Dependencies
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0MDkwMDBhYWE2MjBkN2U1ZDEzMTZiYzQ1Y2ZmNzk5ZiIsIm5iZiI6MTczNjQ5MTYxMC4yMzEsInN1YiI6IjY3ODBjMjVhYTY3NzhhYTViMzdiNTlhNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.D0gG35PvS1Ig90ifqyXNxuK4-vrKQW1fGDNiWb4Mdj0"

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }





}