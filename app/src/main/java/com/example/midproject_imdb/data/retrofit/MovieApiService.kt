package com.example.midproject_imdb.data.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): MovieSearchResponse

    @GET("discover/movie")
    suspend fun getNearbyMovies(
        @Query("region") region: String,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): MovieSearchResponse
}

data class MovieSearchResponse(
    val results: List<MovieResponse>

)

data class MovieResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double
)
