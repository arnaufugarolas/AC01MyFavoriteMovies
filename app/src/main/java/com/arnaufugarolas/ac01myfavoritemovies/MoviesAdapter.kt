package com.arnaufugarolas.ac01myfavoritemovies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arnaufugarolas.ac01myfavoritemovies.databinding.MovieItemBinding

class MoviesAdapter(
    var movies: List<Movie> = emptyList(),
    private val mContext: Context,
    private val onItemClicked: (Movie) -> Unit,
    private val onDeleteClicked: (Movie) -> Unit,
) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.movie_item, parent, false), mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)

        holder.itemView.setOnClickListener { onItemClicked(movie) }

        val binding = MovieItemBinding.bind(holder.itemView)
        binding.delete.setOnClickListener { onDeleteClicked(movie) }
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(val view: View, val mContext: Context) : RecyclerView.ViewHolder(view) {

        private val binding = MovieItemBinding.bind(view)

        fun bind(movie: Movie) {
            binding.title.text = movie.title

            val circularProgressDrawable = CircularProgressDrawable(mContext)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val requestOptions = RequestOptions()
                .circleCrop() // redonda
                .placeholder(circularProgressDrawable)  // loading

            Glide.with(binding.thumb)
                .load(movie.posterPath)
                .apply(requestOptions)
                .into(binding.thumb)


            binding.favorite.setImageResource(if (movie.favorite == true) R.drawable.ic_favorite else 0)
            binding.delete.setImageResource(R.drawable.ic_delete)

        }
    }
}
