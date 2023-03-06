package com.arnaufugarolas.ac01myfavoritemovies

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
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.MovieItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar

interface EditRatingListener {
    fun onEditRating(movie: Movie, rating: Int)
}

class MovieAdapter(val movies: List<Movie>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>(),
    EditRatingListener {

    override fun onEditRating(movie: Movie, rating: Int) {
        movies[movies.indexOf(movie)].myScore = rating
        notifyItemChanged(movies.indexOf(movie))
    }

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
        holder.binding.TVMovieTitle.setOnLongClickListener {
            Snackbar.make(holder.binding.TVMovieTitle, "Movie title", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.TVMovieReleaseDate.setOnLongClickListener {
            Snackbar.make(
                holder.binding.TVMovieReleaseDate,
                "Movie release date",
                Snackbar.LENGTH_SHORT
            ).show()
            true
        }
        holder.binding.TVMovieRating.setOnLongClickListener {
            Snackbar.make(holder.binding.TVMovieRating, "Movie rating", Snackbar.LENGTH_SHORT)
                .show()
            true
        }
        holder.binding.IVFavorite.setOnLongClickListener {
            Snackbar.make(holder.binding.IVFavorite, "Favorite", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVEditRating.setOnLongClickListener {
            Snackbar.make(holder.binding.IVEditRating, "Edit rating", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVReadMore.setOnLongClickListener {
            Snackbar.make(holder.binding.IVReadMore, "Read more", Snackbar.LENGTH_SHORT).show()
            true
        }
        holder.binding.IVMoviePoster.setOnLongClickListener {
            Snackbar.make(holder.binding.IVMoviePoster, "Movie Poster", Snackbar.LENGTH_SHORT)
                .show()
            true
        }
    }

    private fun bind(movie: Movie, holder: ViewHolder) {
        holder.binding.TVMovieTitle.text = movie.title
        holder.binding.TVMovieReleaseDate.text = movie.releaseDate
        holder.binding.TVMovieRating.text = movie.voteAverage.toString()

        if (movie.favorite == true) {
            holder.binding.IVFavorite.setImageResource(R.drawable.sharp_star)
        } else {
            holder.binding.IVFavorite.setImageResource(R.drawable.sharp_star_outline)
        }

        if (movie.posterPath != null) {
            setImageToImageView(
                holder.binding.IVMoviePoster,
                holder.binding.PBMoviePoster,
                holder.binding.IVErrorImage,
                movie.posterPath.toString()
            )
        } else {
            holder.binding.IVErrorImage.visibility = View.VISIBLE
        }

        if (movie.voteAverage!!.compareTo(7) >= 0) {
            holder.binding.TVMovieRating.setTextColor(
                getColor(
                    holder.binding.TVMovieRating.context,
                    R.color.Mantis
                )
            )
        } else if (movie.voteAverage!!.compareTo(5) >= 0) {
            holder.binding.TVMovieRating.setTextColor(
                getColor(
                    holder.binding.TVMovieRating.context,
                    R.color.sunglow
                )
            )
        } else {
            holder.binding.TVMovieRating.setTextColor(
                getColor(
                    holder.binding.TVMovieRating.context,
                    R.color.cornell_red
                )
            )
        }

        setLongClickListeners(holder)

        holder.binding.IVFavorite.setOnClickListener {
            movie.favorite = !movie.favorite!!
            notifyItemChanged(movies.indexOf(movie))
        }

        holder.binding.IVEditRating.setOnClickListener {
            val dialog = EditRatingDialog(movie)
            dialog.show(fragmentManager, "EditRatingDialog")
        }

        holder.binding.IVReadMore.setOnClickListener {
            val intent = Intent(holder.binding.IVReadMore.context, MovieDetails::class.java)
            intent.putExtra("id", movie.id)
            holder.binding.IVReadMore.context.startActivity(intent)
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
        ).setBackgroundTint(getColor(error.context, R.color.cornell_red)).show()

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
