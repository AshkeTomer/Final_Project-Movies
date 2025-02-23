package com.example.midproject_imdb.ui.add_movie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.midproject_imdb.ui.all_movies.MoviesViewModel
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.AddItemLayoutBinding
import com.example.midproject_imdb.data.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddMovieFragment : Fragment() {
    private var _binding: AddItemLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.updateCurrentImageUri(it.toString())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddItemLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextValidation()
        setupObservers()
        setupClickListeners()
        validateInputs()
    }



    private fun setupObservers() {
        viewModel.chosenItem.observe(viewLifecycleOwner) { movie ->
            if (movie != null && viewModel.currentTitle.value == null) {
                viewModel.setEditMode(true, movie.id)
                viewModel.setCurrentValues(
                    movie.title,
                    movie.description,
                    movie.userComments,
                    movie.photo,
                    movie.rating,
                    movie.releaseDate
                )
                viewModel.setShowComments(true)
            }
        }

        viewModel.isEditMode.observe(viewLifecycleOwner) { isEdit ->
            binding.finishBtn.text = if (isEdit)
                getString(R.string.update_movie)
            else
                getString(R.string.add_movie)
        }

        viewModel.showComments.observe(viewLifecycleOwner) { show ->
            binding.userCommentsLayout.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.currentTitle.observe(viewLifecycleOwner) { title ->
            if (binding.itemTitle.text.toString() != title) {
                binding.itemTitle.setText(title)
            }
        }

        viewModel.currentDescription.observe(viewLifecycleOwner) { description ->
            if (binding.itemDescription.text.toString() != description) {
                binding.itemDescription.setText(description)
            }
        }

        viewModel.currentUserComments.observe(viewLifecycleOwner) { comments ->
            if (binding.userComments.text.toString() != comments) {
                binding.userComments.setText(comments)
            }
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.resultImage.setImageURI(Uri.parse(it))
            }
        }

        viewModel.currentRating.observe(viewLifecycleOwner) { rating ->
            if (binding.itemRating.text.toString() != rating.toString()) {
                binding.itemRating.setText(rating.toString())
            }
        }

        viewModel.currentReleaseDate.observe(viewLifecycleOwner) { date ->
            if (binding.itemReleaseDate.text.toString() != date) {
                binding.itemReleaseDate.setText(date)
            }
        }
    }

    private fun setupClickListeners() {
        binding.finishBtn.setOnClickListener {
            if (isInputValid()) {
                val movie = Movie(
                    title = viewModel.currentTitle.value.orEmpty().trim(),
                    description = viewModel.currentDescription.value.orEmpty().trim(),
                    photo = viewModel.currentImageUri.value,
                    userComments = viewModel.currentUserComments.value.orEmpty(),
                    rating = viewModel.currentRating.value ?: 0f,
                    releaseDate = viewModel.currentReleaseDate.value.orEmpty()
                )

                if (viewModel.isEditMode.value == true) {
                    movie.id = viewModel.editMovieId.value ?: 0
                    viewModel.updateMovie(movie)
                    Toast.makeText(context, getString(R.string.movie_updated), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addMovie(movie)
                    Toast.makeText(context, getString(R.string.movie_added), Toast.LENGTH_SHORT).show()
                }

                viewModel.clearCurrentValues()
                findNavController().navigate(R.id.action_addItemFragment_to_allItemsFragment)
            }
        }

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun setupTextValidation() {
        binding.itemTitle.addTextChangedListener(createTextWatcher { text ->
            validateTitle(text)
        })

        binding.itemDescription.addTextChangedListener(createTextWatcher { text ->
            validateDescription(text)
        })

        binding.itemRating.addTextWatcher { text ->
            validateRating(text)
        }

        binding.itemReleaseDate.addTextWatcher { text ->
            validateReleaseDate(text)
        }

        binding.userComments.addTextWatcher { text ->
            viewModel.updateCurrentUserComments(text)
        }
    }

    private fun createTextWatcher(afterTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { afterTextChanged(it) }
            }
        }
    }

    private fun validateTitle(title: String) {
        when {
            title.isBlank() -> {
                binding.titleLayout.error = getString(R.string.title_required)
            }
            title.length < 2 -> {
                binding.titleLayout.error = getString(R.string.title_too_short)
            }
            else -> {
                binding.titleLayout.error = null
                viewModel.updateCurrentTitle(title)
            }
        }
        validateInputs()
    }

    private fun validateDescription(description: String) {
        when {
            description.isBlank() -> {
                binding.descriptionLayout.error = getString(R.string.description_required)
            }
            description.length < 10 -> {
                binding.descriptionLayout.error = getString(R.string.description_too_short)
            }
            else -> {
                binding.descriptionLayout.error = null
                viewModel.updateCurrentDescription(description)
            }
        }
        validateInputs()
    }

    private fun validateRating(rating: String) {
        try {
            when {
                rating.isBlank() -> {
                    binding.ratingLayout.error = getString(R.string.rating_required)
                }
                rating.toFloat() !in 0f..10f -> {
                    binding.ratingLayout.error = getString(R.string.rating_range_error)
                }
                else -> {
                    binding.ratingLayout.error = null
                    viewModel.updateCurrentRating(rating.toFloat())
                }
            }
        } catch (e: NumberFormatException) {
            binding.ratingLayout.error = getString(R.string.invalid_rating_format)
        }
        validateInputs()
    }

    private fun validateReleaseDate(date: String) {
        if (date.isBlank()) {
            binding.releaseDateLayout.error = getString(R.string.release_date_required)
            validateInputs()
            return
        }

        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.isLenient = false
            val parsedDate = dateFormat.parse(date)

            if (parsedDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = parsedDate

                when {
                    calendar.get(Calendar.YEAR) < 1888 -> { // First movie ever made
                        binding.releaseDateLayout.error = getString(R.string.release_date_too_early)
                    }
                    calendar.time.after(Calendar.getInstance().time) -> {
                        binding.releaseDateLayout.error = getString(R.string.release_date_future)
                    }
                    else -> {
                        binding.releaseDateLayout.error = null
                        viewModel.updateCurrentReleaseDate(date)
                    }
                }
            } else {
                binding.releaseDateLayout.error = getString(R.string.invalid_date_format)
            }
        } catch (e: Exception) {
            binding.releaseDateLayout.error = getString(R.string.invalid_date_format)
        }
        validateInputs()
    }

    private fun isInputValid(): Boolean {
        val hasValidTitle = binding.titleLayout.error == null && !binding.itemTitle.text.isNullOrBlank()
        val hasValidDescription = binding.descriptionLayout.error == null && !binding.itemDescription.text.isNullOrBlank()
        val hasValidRating = binding.ratingLayout.error == null && !binding.itemRating.text.isNullOrBlank()
        val hasValidReleaseDate = binding.releaseDateLayout.error == null && !binding.itemReleaseDate.text.isNullOrBlank()

        return hasValidTitle && hasValidDescription && hasValidRating && hasValidReleaseDate
    }

    private fun validateInputs() {
        binding.finishBtn.isEnabled = isInputValid()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.clearCurrentValues()
        }
        _binding = null
    }
}

// Extension function to make TextWatcher creation more concise
private fun android.widget.EditText.addTextWatcher(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s?.toString() ?: "")
        }
    })
}