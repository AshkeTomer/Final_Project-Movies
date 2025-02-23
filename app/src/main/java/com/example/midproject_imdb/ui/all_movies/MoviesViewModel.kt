package com.example.midproject_imdb.ui.all_movies

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.R
import com.example.midproject_imdb.data.models.Movie
import com.example.midproject_imdb.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val application: Application
) : ViewModel() {


    private val _chosenItem = MutableLiveData<Movie?>()
    val chosenItem: LiveData<Movie?> = _chosenItem

    val movies: LiveData<List<Movie>> = repository.getMoviesByAscOrder()

    fun addMovie(movie: Movie) {
        viewModelScope.launch{
        repository.addMovie(movie)}
    }

     fun deleteMovie(movie: Movie){
        viewModelScope.launch { repository.deleteMovie(movie)}
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
        repository.updateMovie(movie)}
    }

    fun setMovie(movie: Movie?){
        viewModelScope.launch {
        _chosenItem.value=movie}
    }

    private val allMovies: LiveData<List<Movie>> = repository.allMovies




    fun preloadMovies() {

        allMovies.observeForever { movies ->
            if (movies.isEmpty()) {
                viewModelScope.launch {
                    repository.insertMovies(
                        listOf(


                            Movie(application.getString(R.string.batman), application.getString(R.string.batman_description), "android.resource://com.example.midproject_imdb/drawable/batman","No comment",6.2f,"20/12/2012"),
                            Movie( application.getString(R.string.superman),  application.getString(R.string.superman_description), "android.resource://com.example.midproject_imdb/drawable/superman","No comment",8f,"15/02/2010"),
                            Movie( application.getString(R.string.one_piece), application.getString(R.string.one_piece_description), "android.resource://com.example.midproject_imdb/drawable/onepiece","No comments",10f,"12/10/1999")
                        )
                    )
                }
            }
        }
    }

    // delete dialog handle
    private val _showDeleteDialog = MutableLiveData<Pair<Movie?, Int>?>()
    val showDeleteDialog: LiveData<Pair<Movie?, Int>?> = _showDeleteDialog
    fun setDeleteDialog(movie: Movie?, position: Int) {
        _showDeleteDialog.value = Pair(movie, position)
    }
    fun clearDeleteDialog() {
        _showDeleteDialog.value = null
    }

  //handling configuration changes in creation and editing

    private val _currentTitle = MutableLiveData<String?>()
    val currentTitle: MutableLiveData<String?> = _currentTitle

    private val _currentDescription = MutableLiveData<String?>()
    val currentDescription: MutableLiveData<String?> = _currentDescription

    private val _currentUserComments = MutableLiveData<String?>()
    val currentUserComments: MutableLiveData<String?> = _currentUserComments

    private val _currentImageUri = MutableLiveData<String?>()
    val currentImageUri: MutableLiveData<String?> = _currentImageUri

    private val _currentRating = MutableLiveData<Float>()
    val currentRating: LiveData<Float> = _currentRating

    private val _currentReleaseDate = MutableLiveData<String?>()
    val currentReleaseDate: MutableLiveData<String?> = _currentReleaseDate

    fun updateCurrentRating(rating: Float) {
        _currentRating.value = rating
    }

    fun updateCurrentReleaseDate(date: String) {
        _currentReleaseDate.value = date
    }


    fun setCurrentValues(title: String?, description: String?, comments: String?, imageUri: String?, rating: Float?, releaseDate: String?) {
        _currentTitle.value = title
        _currentDescription.value = description
        _currentUserComments.value = comments
        _currentImageUri.value = imageUri
        _currentRating.value = rating ?: 0f  // Default to 0f if null
        _currentReleaseDate.value = releaseDate

    }

    fun clearCurrentValues() {
        _currentTitle.value = null
        _currentDescription.value = null
        _currentUserComments.value = null
        _currentImageUri.value = null
        _showComments.value = false  // Reset the visibility state
        _isEditMode.value = false
        _editMovieId.value = 0
        _currentRating.value =  0f
        _currentReleaseDate.value = null

    }

    fun updateCurrentTitle(title: String) {
        _currentTitle.value = title
    }

    fun updateCurrentDescription(description: String) {
        _currentDescription.value = description
    }

    fun updateCurrentUserComments(comments: String) {
        _currentUserComments.value = comments
    }

    fun updateCurrentImageUri(uri: String) {
        _currentImageUri.value = uri
    }

    // handled comments visibility  in edit mode

    private val _showComments = MutableLiveData<Boolean>()
    val showComments: LiveData<Boolean> = _showComments

    fun setShowComments(show: Boolean) {
        _showComments.value = show
    }

    //edit mode state in the ViewModel

    private val _isEditMode = MutableLiveData<Boolean>()
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _editMovieId = MutableLiveData<Int>()
    val editMovieId: LiveData<Int> = _editMovieId

    fun setEditMode(isEdit: Boolean, movieId: Int = 0) {
        _isEditMode.value = isEdit
        _editMovieId.value = movieId
    }


}