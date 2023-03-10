package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMovieDetailsBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory

class MovieDetails : AppCompatActivity() {
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
            if (it != null) {
                binding.TVDetailsMovieTitle.text = it.title
                binding.TVDetailsDataReleaseDate.text = it.releaseDate
                binding.TVDetailsDataAdult.text = it.adult.toString()
                binding.TVDetailsDataMyScore.text = it.myScore.toString()
                binding.TVDetailsDataOverview.text = it.overview
                binding.TVDetailsDataPopularity.text = it.popularity.toString()
                binding.TVDetailsDataOriginalTitle.text = it.originalTitle
                binding.TVDetailsDataVoteCount.text = it.voteCount.toString()
                binding.TVDetailsDataVoteAverage.text = it.voteAverage.toString()
            }
        }
    }
}
