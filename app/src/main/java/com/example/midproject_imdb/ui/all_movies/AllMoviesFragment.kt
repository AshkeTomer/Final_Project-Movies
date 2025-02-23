package com.example.midproject_imdb.ui.all_movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.midproject_imdb.R
import com.example.midproject_imdb.data.models.Movie
import com.example.midproject_imdb.databinding.AllItemsLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllMoviesFragment : Fragment() {
    private var _binding : AllItemsLayoutBinding? = null
    private val binding get() = _binding!!
    val viewModel : MoviesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AllItemsLayoutBinding.inflate(inflater,container,false)

        binding.fab.setOnClickListener {
            viewModel.setMovie(null)
            findNavController().navigate(R.id.action_allItemsFragment_to_addItemFragment)
        }

        binding.goToSearch.setOnClickListener {
            findNavController().navigate(R.id.action_allItemsFragment_to_movieSearchFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it, Toast.LENGTH_SHORT).show()
        }
        viewModel.preloadMovies()
        setupObservers()
        setupSwipeToDelete()
    }

    private fun setupObservers() {
        viewModel.showDeleteDialog.observe(viewLifecycleOwner) { dialogState ->
            dialogState?.let { (movie, position) ->
                showDeleteDialog(movie, position)
            }
        }

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            binding.recycler.apply {
                adapter = MovieAdapter(movies, object : MovieAdapter.MovieListener {
                    override fun onItemClicked(index: Int) {
                        viewModel.setMovie(movies[index])
                        findNavController().navigate(R.id.action_allItemsFragment_to_detailItemFragment)
                    }

                    override fun onItemLongClicked(index: Int) {
                        viewModel.setMovie(movies[index])
                        findNavController().navigate(R.id.action_allItemsFragment_to_addItemFragment)
                    }
                })
                layoutManager = GridLayoutManager(requireContext(), 1)
            }
        }
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = (binding.recycler.adapter as MovieAdapter).itemAt(position)
                    viewModel.setDeleteDialog(movie, position)
                }
            }
        }).attachToRecyclerView(binding.recycler)
    }

    private fun showDeleteDialog(movie: Movie?, position: Int) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.delete_movie))
            setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_movie))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                movie?.let { viewModel.deleteMovie(it) }
                viewModel.clearDeleteDialog()
                Toast.makeText(requireContext(),
                    getString(R.string.movie_deleted), Toast.LENGTH_SHORT).show()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ ->
                binding.recycler.adapter?.notifyItemChanged(position)
                viewModel.clearDeleteDialog()
                dialog.dismiss()
            }
            setOnCancelListener {
                binding.recycler.adapter?.notifyItemChanged(position)
                viewModel.clearDeleteDialog()
            }
        }.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
