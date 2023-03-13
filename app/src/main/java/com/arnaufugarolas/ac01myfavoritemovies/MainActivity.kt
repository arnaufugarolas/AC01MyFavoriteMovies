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
import com.arnaufugarolas.ac01myfavoritemovies.enumClass.SortType
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
    private var sortType = SortType.TITLE_ASC

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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        sortType = SortType.valueOf(savedInstanceState.getString("sortType")!!)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("sortType", sortType.name)
        super.onSaveInstanceState(outState)
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

        menu.findItem(R.id.MISortTitleAsc).setOnMenuItemClickListener {
            sortType = SortType.TITLE_ASC
            checkMovies(sortMovies(adapter.movies))

            true
        }

        menu.findItem(R.id.MISortTitleDesc).setOnMenuItemClickListener {
            sortType = SortType.TITLE_DESC
            checkMovies(sortMovies(adapter.movies))
            true
        }

        menu.findItem(R.id.MISortRatingAsc).setOnMenuItemClickListener {
            sortType = SortType.RATING_ASC
            checkMovies(sortMovies(adapter.movies))
            true
        }

        menu.findItem(R.id.MISortRatingDesc).setOnMenuItemClickListener {
            sortType = SortType.RATING_DESC
            checkMovies(sortMovies(adapter.movies))
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
            checkMovies(sortMovies(movies))
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

    private fun sortMovies(movies: MutableList<Movie>): MutableList<Movie> {
        val favoriteMovies = movies.filter { it.favorite == true }.toMutableList()
        Log.d("SORT", "sortMovies: $favoriteMovies")
        return when (sortType) {
            SortType.TITLE_ASC -> favoriteMovies.sortedBy { it.title }
            SortType.TITLE_DESC -> favoriteMovies.sortedByDescending { it.title }
            SortType.RATING_ASC -> favoriteMovies.sortedBy { it.voteAverage }
            SortType.RATING_DESC -> favoriteMovies.sortedByDescending { it.voteAverage }
        }.toMutableList()
    }

    private fun checkMovies(movies: MutableList<Movie> = viewModel.movies.value!!) {
        val oldMovies = adapter.movies
        val differences = getMutableListDifferences(oldMovies, movies)

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

                DifferenceType.MOVED -> {
                    val oldIndex = adapter.movies.indexOf(difference.newItem)
                    adapter.movies.removeAt(oldIndex)
                    adapter.movies.add(difference.newIndex!!, difference.newItem!!)
                    binding.RVMainMovies.adapter?.notifyItemMoved(oldIndex, difference.newIndex!!)
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

        for (newItem in newList) {
            if (oldList.contains(newItem)) {
                val oldIndex = oldList.indexOf(newItem)
                val newIndex = newList.indexOf(newItem)
                if (oldIndex != newIndex) {
                    differences.add(Difference(DifferenceType.MOVED, null, newItem, newIndex))
                }
            }
        }

        return differences
    }
}
