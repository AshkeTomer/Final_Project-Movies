package com.example.midproject_imdb.ui.MovieTMDBDetailed


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MovieTMDBDetailViewModel  @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieTitle = MutableLiveData<String>()
    val movieTitle: LiveData<String> = _movieTitle

    private val _movieOverview = MutableLiveData<String>()
    val movieOverview: LiveData<String> = _movieOverview

    private val _movieRating = MutableLiveData<Float>()
    val movieRating: LiveData<Float> = _movieRating

    private val _movieReleaseDate = MutableLiveData<String?>()
    val movieReleaseDate: LiveData<String?> = _movieReleaseDate

    private val _moviePosterPath = MutableLiveData<String?>()
    val moviePosterPath: LiveData<String?> = _moviePosterPath

    init {
        // Restore saved state
        savedStateHandle.get<String>("title")?.let { _movieTitle.value = it }
        savedStateHandle.get<String>("overview")?.let { _movieOverview.value = it }
        savedStateHandle.get<Float>("rating")?.let { _movieRating.value = it }
        savedStateHandle.get<String>("releaseDate")?.let { _movieReleaseDate.value = it }
        savedStateHandle.get<String>("posterPath")?.let { _moviePosterPath.value = it }
    }

    fun setMovieDetails(
        title: String,
        overview: String,
        rating: Float,
        releaseDate: String?,
        posterPath: String?
    ) {
        _movieTitle.value = title
        savedStateHandle["title"] = title

        _movieOverview.value = overview
        savedStateHandle["overview"] = overview

        _movieRating.value = rating
        savedStateHandle["rating"] = rating

        _movieReleaseDate.value = releaseDate
        savedStateHandle["releaseDate"] = releaseDate

        _moviePosterPath.value = posterPath
        savedStateHandle["posterPath"] = posterPath
    }

    fun getFullPosterUrl(path: String): String {
        return "https://image.tmdb.org/t/p/w500$path"
    }
}