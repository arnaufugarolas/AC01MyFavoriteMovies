package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMovieDetailsBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MovieDetails : AppCompatActivity(), EditRatingListener {
    private lateinit var binding: ActivityMovieDetailsBinding

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.TVDetailsDataOverview.movementMethod = ScrollingMovementMethod()

        val movieId = intent.getIntExtra("id", -1)
        if (movieId == -1) {
            finish()
        }
        viewModel.getMovieDetails(movieId)

        init()
    }

    private fun init() {
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.movieDetails.observe(this) {
            val movie = it
            if (movie != null) {
                binding.TVDetailsMovieTitle.text = movie.title
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                binding.TVDetailsDataReleaseDate.text =
                    outputFormat.format(inputFormat.parse(movie.releaseDate!!)!!)
                binding.TVDetailsDataAdult.text = movie.adult.toString()
                binding.TVDetailsDataMyScore.text = movie.myScore.toString()
                binding.TVDetailsDataOverview.text = movie.overview
                binding.TVDetailsDataPopularity.text = movie.popularity.toString()
                binding.TVDetailsDataOriginalTitle.text = movie.originalTitle
                binding.TVDetailsDataVoteCount.text = movie.voteCount.toString()
                binding.TVDetailsDataVoteAverage.text = movie.voteAverage.toString()

                binding.IVDetailsEditRating.setOnClickListener {
                    val dialog = EditRatingDialog(movie)
                    dialog.show(supportFragmentManager, "EditRatingDialog")
                }
            }
        }

        viewModel.loadingSingleMovie.observe(this) {
            if (it) {
                binding.PBDetailsLoading.visibility = android.view.View.VISIBLE
                binding.SVDetails.visibility = android.view.View.GONE
            } else {
                binding.PBDetailsLoading.visibility = android.view.View.GONE
                binding.SVDetails.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onEditRating(movie: Movie, rating: Int) {
        val editedMovie = movie.copy()
        editedMovie.myScore = rating
        viewModel.onMovieUpdate(editedMovie)
    }
}
