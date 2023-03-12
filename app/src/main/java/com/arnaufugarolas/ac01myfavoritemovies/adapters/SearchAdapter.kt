package com.arnaufugarolas.ac01myfavoritemovies.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaufugarolas.ac01myfavoritemovies.AddMovieDialog
import com.arnaufugarolas.ac01myfavoritemovies.R
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.SearchItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class SearchAdapter(
    var movies: MutableList<Movie>,
    private val fragmentManager: FragmentManager,
    private val onMovieAdd: (Movie) -> Unit,
) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        bind(movie, holder)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    private fun setLongClickListeners(holder: ViewHolder) {
        holder.binding.TVSearchItemMovieTitle.setOnLongClickListener {
            Snackbar.make(it, "Movie title", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.TVSearchItemMovieReleaseDate.setOnLongClickListener {
            Snackbar.make(it, "Movie release date", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.TVSearchItemMoviePopularity.setOnLongClickListener {
            Snackbar.make(it, "Movie popularity", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVSearchItemMoviePoster.setOnLongClickListener {
            Snackbar.make(it, "Movie poster", Snackbar.LENGTH_SHORT).show()
            true
        }
    }

    private fun bind(movie: Movie, holder: ViewHolder) {
        holder.binding.TVSearchItemMovieTitle.text = movie.title // Set the title

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            holder.binding.TVSearchItemMovieReleaseDate.text =
                outputFormat.format(inputFormat.parse(movie.releaseDate!!)!!) // Set the release date
        } catch (e: Exception) {
            holder.binding.TVSearchItemMovieReleaseDate.text =
                movie.releaseDate // Set the release date
        }
        holder.binding.TVSearchItemMoviePopularity.text =
            movie.popularity.toString() // Set the popularity

        if (movie.posterPath != null) { // If there is a poster, show it
            setImageToImageView(
                holder.binding.IVSearchItemMoviePoster,
                holder.binding.PBSearchItemMoviePoster,
                holder.binding.IVSearchItemErrorImage,
                "https://image.tmdb.org/t/p/original${movie.posterPath.toString()}"
            )
        } else { // If there is no poster, show the error image
            holder.binding.IVSearchItemErrorImage.visibility = View.VISIBLE
        }

        setLongClickListeners(holder) // Set the long click listeners

        holder.binding.IVSearchItemAdd.setOnClickListener {
            val dialog = AddMovieDialog(movie)
            dialog.show(fragmentManager, "AddMovieDialog")
        }
    }

    private fun setImageToImageView(
        imageView: ImageView,
        loading: ProgressBar,
        error: ImageView,
        imageURL: String,
    ) {
        loading.visibility = ProgressBar.VISIBLE
        error.visibility = ImageView.GONE

        Glide.with(imageView.context)
            .load(imageURL)
            .listener((object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return loadFailed(
                        imageView,
                        imageURL,
                        error,
                        loading
                    )
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return resourceReady(loading, imageView)
                }
            })).into(imageView)
    }

    private fun loadFailed(
        target: ImageView,
        imageURL: String,
        error: ImageView,
        loading: ProgressBar,
    ): Boolean {
        loading.visibility = ProgressBar.GONE
        target.visibility = ImageView.GONE
        error.visibility = ImageView.VISIBLE

        Snackbar.make(
            error, "Error loading the image", Snackbar.LENGTH_SHORT
        ).setBackgroundTint(getColor(error.context, R.color.cornell_red))
            .setTextColor(getColor(error.context, R.color.white)).show()

        error.setOnClickListener {
            it.visibility = ImageView.GONE
            setImageToImageView(target, loading, error, imageURL)
        }

        error.setOnLongClickListener {
            Snackbar.make(it, "Error loading the image", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(it.context, R.color.cornell_red)).show()
            true
        }
        return false
    }

    private fun resourceReady(loading: ProgressBar, imageView: ImageView): Boolean {
        imageView.visibility = ImageView.VISIBLE
        loading.visibility = ProgressBar.GONE
        return false
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SearchItemBinding = SearchItemBinding.bind(itemView)
    }
}
