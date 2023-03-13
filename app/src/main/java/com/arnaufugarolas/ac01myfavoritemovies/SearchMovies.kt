package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaufugarolas.ac01myfavoritemovies.adapters.SearchAdapter
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivitySearchMoviesBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

interface AddMovieListener {
    fun addMovie(movie: Movie, rating: Int)
}

class SearchMovies : AppCompatActivity(), AddMovieListener {
    private lateinit var binding: ActivitySearchMoviesBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private val adapter = SearchAdapter(mutableListOf(), supportFragmentManager) {
        viewModel.addMovie(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupRecyclerView()
        setupOnClickListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.RVSearchMovies.adapter = adapter
        binding.RVSearchMovies.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun setupOnClickListeners() {
        binding.IVSearchSearch.setOnClickListener {
            val movieName = binding.ETSearchQuery.text.toString()
            viewModel.searchMovies(movieName)
        }
    }

    private fun setupObservers() {
        viewModel.searchedMovies.observe(this) {
            adapter.movies = it
            adapter.movies.sortBy { movie -> movie.title }
            adapter.notifyDataSetChanged()
        }

        viewModel.searching.observe(this) {
            binding.PBSearchLoading.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.addMovieError.observe(this) {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.addingMovie.observe(this) {
            binding.PBSearchLoading.visibility = if (it) View.VISIBLE else View.GONE
            binding.RVSearchMovies.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.addedMovie.observe(this) {
            if (it) {
                Snackbar.make(
                    binding.root,
                    "Movie added to favorites",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun addMovie(movie: Movie, rating: Int) {
        val newMovie = movie.copy()
        newMovie.myScore = rating
        newMovie.favorite = true
        newMovie.backdropPath = "https://image.tmdb.org/t/p/original${newMovie.backdropPath}"
        newMovie.posterPath = "https://image.tmdb.org/t/p/original${newMovie.posterPath}"
        viewModel.addMovie(newMovie)
    }
}
