package com.example.midproject_imdb.ui.add_movie

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

@AndroidEntryPoint
class AddMovieFragment : Fragment() {
    private var _binding: AddItemLayoutBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var isEditMode = false
    private var EditMovie = 0

    val viewModel: MoviesViewModel by activityViewModels()

    val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.resultImage.setImageURI(it)
            if (it != null) {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUri = it
                viewModel.updateCurrentImageUri(it.toString())
            }
        }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddItemLayoutBinding.inflate(inflater, container, false)

        setupTextValidation()
        setupObservers()
        setupClickListeners()
        validateInputs()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.chosenItem.observe(viewLifecycleOwner) { movie ->
            if (movie != null && viewModel.currentTitle.value == null) {
                viewModel.setEditMode(true, movie.id)
                viewModel.setCurrentValues(
                    movie.title,
                    movie.description,
                    movie.userComments,
                    movie.photo
                )
                viewModel.setShowComments(true)  // Set the visibility state
            }
        }



        viewModel.isEditMode.observe(viewLifecycleOwner) { isEdit ->
            isEditMode = isEdit
        }
        viewModel.editMovieId.observe(viewLifecycleOwner) { id ->
            EditMovie = id
        }


        // observer for comments visibility
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
            if (uri != null) {
                imageUri = Uri.parse(uri)
                binding.resultImage.setImageURI(imageUri)
            }
        }
    }

    private fun setupClickListeners() {
        binding.finishBtn.setOnClickListener {
            if (isInputValid()) {
                if (isEditMode) {
                    val item = Movie(
                        binding.itemTitle.text.toString().trim(),
                        binding.itemDescription.text.toString().trim(),
                        imageUri.toString(),
                        binding.userComments.text.toString()
                    )
                    item.id = EditMovie
                    viewModel.updateMovie(item)
                } else {
                    val item = Movie(
                        binding.itemTitle.text.toString().trim(),
                        binding.itemDescription.text.toString().trim(),
                        imageUri.toString()
                    )
                    viewModel.addMovie(item)
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
        binding.itemTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { viewModel.updateCurrentTitle(it) }
                validateInputs()
            }
        })

        binding.itemDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { viewModel.updateCurrentDescription(it) }
                validateInputs()
            }
        })

        binding.userComments.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { viewModel.updateCurrentUserComments(it) }
            }
        })
    }

    private fun isInputValid(): Boolean {
        val titleText = binding.itemTitle.text.toString().trim()
        val descriptionText = binding.itemDescription.text.toString().trim()
        return titleText.isNotEmpty() && descriptionText.isNotEmpty()
    }

    private fun validateInputs() {
        val isValid = isInputValid()
        binding.finishBtn.isEnabled = isValid

        if (binding.itemTitle.text.toString().trim().isEmpty()) {
            binding.itemTitle.error = getString(R.string.title_required)
        } else {
            binding.itemTitle.error = null
        }

        if (binding.itemDescription.text.toString().trim().isEmpty()) {
            binding.itemDescription.error = getString(R.string.description_required)
        } else {
            binding.itemDescription.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.clearCurrentValues()
        }
        _binding = null
    }
}