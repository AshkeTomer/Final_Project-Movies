package com.example.midproject_imdb.ui.detail_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.midproject_imdb.databinding.DetailItemLayoutBinding
import com.example.midproject_imdb.ui.all_movies.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedMovieFragment : Fragment() {
    private var _binding : DetailItemLayoutBinding?  = null
    private val viewModel : MoviesViewModel by activityViewModels()
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailItemLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.chosenItem.observe(viewLifecycleOwner) { movie ->
            movie?.let { it ->
                binding.itemTitle.text = it.title
                binding.itemDesc.text = it.description
                binding.userComm.text = it.userComments
                Glide.with(requireContext()).load(it.photo).circleCrop()
                    .into(binding.itemImage)
                binding.itemRating.text = it.rating.toString()
                binding.itemReleaseDate.text  = it.releaseDate
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}