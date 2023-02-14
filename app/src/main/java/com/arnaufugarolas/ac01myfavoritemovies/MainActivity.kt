package com.arnaufugarolas.ac01myfavoritemovies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMainBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private lateinit var binding: ActivityMainBinding

    private val adapter = MoviesAdapter(emptyList(), this,
        {viewModel.onMovieClicked(it) },
        {viewModel.onMovieDelete(it)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
