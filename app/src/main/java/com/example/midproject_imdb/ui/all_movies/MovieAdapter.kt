package com.example.midproject_imdb.ui.all_movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midproject_imdb.databinding.ItemLayoutBinding
import com.example.midproject_imdb.data.models.Movie


class MovieAdapter(val movies:List<Movie>, val callback: MovieListener)
    : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

        interface MovieListener {
        fun onItemClicked(index:Int)
        fun onItemLongClicked(index:Int)
    }

    inner class MovieViewHolder(private val binding: ItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                callback.onItemClicked(position)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                callback.onItemLongClicked(position)
                return true
            }
            return false
        }

        fun bind(movie: Movie) {
            binding.itemTitle.text = movie.title
            Glide.with(binding.root).load(movie.photo).circleCrop()
                .into(binding.itemImage)
        }
    }

    fun itemAt(position:Int) = movies[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bind(movies[position])

    override fun getItemCount() =
        movies.size
}