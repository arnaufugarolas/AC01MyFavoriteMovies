package com.arnaufugarolas.ac01myfavoritemovies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaufugarolas.ac01myfavoritemovies.adapters.MovieAdapter
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Difference
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMainBinding
import com.arnaufugarolas.ac01myfavoritemovies.enumClass.DifferenceType
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

interface EditRatingListener {
    fun onEditRating(movie: Movie, rating: Int)
}

class MainActivity : AppCompatActivity(), EditRatingListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private var adapter: MovieAdapter = MovieAdapter(
        mutableListOf(),
        supportFragmentManager,
        { viewModel.onMovieDelete(it) },
        { viewModel.onMovieUpdate(it) }
    )

    override fun onEditRating(movie: Movie, rating: Int) {
        val editedMovie = movie.copy()
        editedMovie.myScore = rating
        viewModel.onMovieUpdate(editedMovie)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadMovies()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        setupMenu(menu!!)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupMenu(menu: Menu) {
        supportActionBar?.title = "My Favorite Movies"
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        menu.findItem(R.id.MIAddMovie).setOnMenuItemClickListener {
            startActivity(Intent(applicationContext, SearchMovies::class.java))
            true
        }

    }

    private fun init() {
        setupRecyclerView()
        setupObservers()
        setupLongClick()
    }

    private fun setupRecyclerView() {
        binding.RVMainMovies.adapter = adapter
        binding.RVMainMovies.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun setupObservers() {
        viewModel.movies.observeForever { movies ->
            Log.d("Movies", movies.size.toString())
            checkMovies(movies)
        }

        viewModel.loadingMovies.observe(this) {
            binding.PBMainLoading.visibility = if (it) View.VISIBLE else View.GONE
            if (adapter.movies.isEmpty() && !it) {
                binding.IVMainError.visibility = View.VISIBLE
            } else {
                binding.IVMainError.visibility = View.GONE
            }
        }

        viewModel.errorApiRest.observe(this) {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getColor(R.color.cornell_red))
                    .setTextColor(getColor(R.color.white))
                    .show()
            }
        }
    }

    private fun checkMovies(movies: MutableList<Movie> = viewModel.movies.value!!) {
        val differences = getMutableListDifferences(adapter.movies, movies.toMutableList())
        for (difference in differences) {
            when (difference.type) {
                DifferenceType.ADDED -> {
                    adapter.movies.add(difference.newIndex!!, difference.newItem!!)
                    binding.RVMainMovies.adapter?.notifyItemInserted(
                        adapter.movies.indexOf(difference.newItem)
                    )
                }

                DifferenceType.DELETED -> {
                    val index = adapter.movies.indexOf(difference.oldItem)
                    adapter.movies.removeAt(index)
                    binding.RVMainMovies.adapter?.notifyItemRemoved(index)
                }
            }
        }
        if (movies.isEmpty() && !viewModel.loadingMovies.value!!) {
            binding.IVMainError.visibility = View.VISIBLE
        } else {
            binding.IVMainError.visibility = View.GONE
        }
    }

    private fun setupLongClick() {
        binding.IVMainError.setOnLongClickListener {
            Snackbar.make(binding.root, "No data found, click to retry", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.cornell_red))
                .setTextColor(getColor(R.color.white))
                .show()
            true
        }

        binding.IVMainError.setOnClickListener {
            viewModel.loadMovies()
        }

        binding.PBMainLoading.setOnLongClickListener {
            Snackbar.make(binding.root, "Loading data", Snackbar.LENGTH_LONG)
                .setBackgroundTint(
                    getColor(
                        R.color.oxford_blue
                    )
                )
                .setTextColor(getColor(R.color.tea_green))
                .show()
            true
        }
    }

    private fun <Movie> getMutableListDifferences(
        oldList: MutableList<Movie>,
        newList: MutableList<Movie>
    ): MutableList<Difference<Movie>> {
        val differences = mutableListOf<Difference<Movie>>()

        for (oldItem in oldList) {
            if (!newList.contains(oldItem)) {
                differences.add(Difference(DifferenceType.DELETED, oldItem, null, null))
            }
        }

        for (newItem in newList) {
            if (!oldList.contains(newItem)) {
                val index = newList.indexOf(newItem)
                differences.add(Difference(DifferenceType.ADDED, null, newItem, index))
            }
        }

        return differences
    }
}
