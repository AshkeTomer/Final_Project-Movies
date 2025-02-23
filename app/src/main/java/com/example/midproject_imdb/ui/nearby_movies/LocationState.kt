package com.example.midproject_imdb.ui.nearby_movies

sealed class LocationState {
    data object Loading : LocationState()
    data object PermissionDenied : LocationState()
    data object GPSDisabled : LocationState()
    data object NoInternetConnection : LocationState()
    data object LocationUnavailable : LocationState()
    data class LocationReady(val countryCode: String, val countryName: String) : LocationState()
    data class Error(val message: String) : LocationState()
}