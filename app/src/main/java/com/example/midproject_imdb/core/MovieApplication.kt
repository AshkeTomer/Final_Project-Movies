package com.example.midproject_imdb.core

import android.app.Application
import com.example.midproject_imdb.data.local_db.MovieTMDBDatabase
import com.example.midproject_imdb.data.repositories.MovieRepo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApplication : Application()

