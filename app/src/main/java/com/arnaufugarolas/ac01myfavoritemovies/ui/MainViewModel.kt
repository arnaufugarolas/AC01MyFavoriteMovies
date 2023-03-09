package com.arnaufugarolas.ac01myfavoritemovies.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.server.RetrofitConnection
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class MainViewModel : ViewModel() {
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading
    private val _movies = MutableLiveData<MutableList<Movie>>(emptyList())
    val movies: LiveData<MutableList<Movie>> get() = _movies
    private val _errorApiRest = MutableLiveData<String?>(null)
    val errorApiRest: LiveData<String?> get() = _errorApiRest

    val tmdbApiKey: String = "dfe8494cd7c331306cfcc31e6fdeedc4"
    val weatherApiKey: String = "7f7390123a564931910160705231002"

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null

            try {
                val response = RetrofitConnection.service.listMovies()
                if (response.isSuccessful) {
                    _movies.value = response.body()?.toMutableList()
                } else {
                    _errorApiRest.value = response.errorBody().toString()
                }

                _loading.value = false
            } catch (e: Exception) {
                _errorApiRest.value = e.message
                _loading.value = false
            }
        }
    }

    fun onMovieUpdate(movie: Movie) {
        viewModelScope.launch {
            try {
                RetrofitConnection.service.updateMovie(movie.id!!, movie)
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            }
        }.invokeOnCompletion {
            loadMovies()
        }
    }

    fun onMovieDelete(movie: Movie) {
        viewModelScope.launch {
            try {
                RetrofitConnection.service.deleteMovie(movie.id!!)
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            }
        }.invokeOnCompletion {
            loadMovies()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}
