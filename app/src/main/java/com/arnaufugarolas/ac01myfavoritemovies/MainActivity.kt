package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMainBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private var movies: MutableList<Movie> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.movies.observe(this) {
            binding.RVMainMovies.adapter = MovieAdapter(it, supportFragmentManager)
            binding.RVMainMovies.adapter?.notifyDataSetChanged()
        }

        viewModel.loading.observe(this) {
            binding.PBMainLoading.visibility = if (it) View.VISIBLE else View.GONE
        }

        init()
    }

    private fun init() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.RVMainMovies.adapter = MovieAdapter(movies, supportFragmentManager)
        binding.RVMainMovies.layoutManager = LinearLayoutManager(applicationContext)
    }
}
