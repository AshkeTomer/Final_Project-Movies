package com.example.midproject_imdb.ui.all_movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midproject_imdb.data.models.Movie
import com.example.midproject_imdb.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {


    private val _chosenItem = MutableLiveData<Movie?>()
    val chosenItem: LiveData<Movie?> = _chosenItem

       val movies: LiveData<List<Movie>>? = repository.getMovies()

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

    val allMovies: LiveData<List<Movie>>? = repository.allMovies




    fun preloadMovies() {

        allMovies?.observeForever { movies ->
            if (movies.isEmpty()) {
                viewModelScope.launch {
                    repository.insertMovies(
                        listOf(
                            Movie( "Batman", "The dark knight", "android.resource://com.example.midproject_imdb/drawable/batman"),
                            Movie( "Superman", "the man of steel", "android.resource://com.example.midproject_imdb/drawable/superman"),
                            Movie( "One Piece", "the king of the pirates", "android.resource://com.example.midproject_imdb/drawable/onepiece")
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

    fun setCurrentValues(title: String?, description: String?, comments: String?, imageUri: String?) {
        _currentTitle.value = title
        _currentDescription.value = description
        _currentUserComments.value = comments
        _currentImageUri.value = imageUri
    }

    fun clearCurrentValues() {
        _currentTitle.value = null
        _currentDescription.value = null
        _currentUserComments.value = null
        _currentImageUri.value = null
        _showComments.value = false  // Reset the visibility state
        _isEditMode.value = false
        _editMovieId.value = 0

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