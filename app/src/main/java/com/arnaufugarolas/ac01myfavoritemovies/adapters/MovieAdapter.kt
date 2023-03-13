package com.arnaufugarolas.ac01myfavoritemovies.adapters

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaufugarolas.ac01myfavoritemovies.EditRatingDialog
import com.arnaufugarolas.ac01myfavoritemovies.MovieDetails
import com.arnaufugarolas.ac01myfavoritemovies.R
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.MovieItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale


class MovieAdapter(
    var movies: MutableList<Movie>,
    private val fragmentManager: FragmentManager,
    private val onMovieDelete: (Movie) -> Unit,
    private val onMovieUpdate: (Movie) -> Unit
) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
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
        holder.binding.TVMovieItemTitle.setOnLongClickListener {
            Snackbar.make(it, "Movie title", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.TVMovieItemReleaseDate.setOnLongClickListener {
            Snackbar.make(it, "Movie release date", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.TVMovieItemRating.setOnLongClickListener {
            Snackbar.make(it, "Movie rating", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVMovieItemFavorite.setOnLongClickListener {
            Snackbar.make(it, "Favorite", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVMovieItemEditRating.setOnLongClickListener {
            Snackbar.make(it, "Edit personal rating", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVMovieItemReadMore.setOnLongClickListener {
            Snackbar.make(it, "Read more", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVMovieItemPoster.setOnLongClickListener {
            Snackbar.make(it, "Movie poster", Snackbar.LENGTH_SHORT).show()
            true
        }
    }

    private fun bind(movie: Movie, holder: ViewHolder) {
        holder.binding.TVMovieItemTitle.text = movie.title // Set the title
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        holder.binding.TVMovieItemReleaseDate.text =
            outputFormat.format(inputFormat.parse(movie.releaseDate!!)!!) // Set the release date
        holder.binding.TVMovieItemRating.text = movie.voteAverage.toString() // Set the rating

        if (movie.favorite == true) { // If the movie is favorite, show the star
            holder.binding.IVMovieItemFavorite.setImageResource(R.drawable.sharp_star)
        } else { // If the movie is not favorite, show the outline star
            holder.binding.IVMovieItemFavorite.setImageResource(R.drawable.sharp_star_outline)
        }

        if (movie.posterPath != null) { // If there is a poster, show it
            setImageToImageView(
                holder.binding.IVMovieItemPoster,
                holder.binding.PBMovieItemPoster,
                holder.binding.IVMovieItemErrorImage,
                movie.posterPath.toString()
            )
        } else { // If there is no poster, show the error image
            holder.binding.IVMovieItemErrorImage.visibility = View.VISIBLE
        }

        if (movie.voteAverage!!.compareTo(7) >= 0) { // Set the color of the rating
            holder.binding.TVMovieItemRating.setTextColor(
                getColor(
                    holder.binding.TVMovieItemRating.context,
                    R.color.Mantis
                )
            )
        } else if (movie.voteAverage!!.compareTo(5) >= 0) { // Set the color of the rating
            holder.binding.TVMovieItemRating.setTextColor(
                getColor(
                    holder.binding.TVMovieItemRating.context,
                    R.color.sunglow
                )
            )
        } else { // Set the color of the rating
            holder.binding.TVMovieItemRating.setTextColor(
                getColor(
                    holder.binding.TVMovieItemRating.context,
                    R.color.cornell_red
                )
            )
        }

        setLongClickListeners(holder) // Set the long click listeners

        holder.binding.IVMovieItemFavorite.setOnClickListener {
            onMovieDelete(movie)
        }

        holder.binding.IVMovieItemEditRating.setOnClickListener {
            val dialog = EditRatingDialog(movie)
            dialog.show(fragmentManager, "EditRatingDialog")
        }

        holder.binding.IVMovieItemReadMore.setOnClickListener {
            val intent =
                Intent(holder.binding.IVMovieItemReadMore.context, MovieDetails::class.java)
            intent.putExtra("id", movie.id)
            holder.binding.IVMovieItemReadMore.context.startActivity(intent)
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
        val binding: MovieItemBinding = MovieItemBinding.bind(itemView)
    }
}
