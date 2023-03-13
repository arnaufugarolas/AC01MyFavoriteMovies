package com.arnaufugarolas.ac01myfavoritemovies.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arnaufugarolas.ac01myfavoritemovies.Secrets.TMDB_API_KEY
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.server.RetrofitConnectionJSONServer
import com.arnaufugarolas.ac01myfavoritemovies.server.RetrofitConnectionTMDB
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _loadingMovies = MutableLiveData(false)
    val loadingMovies: LiveData<Boolean> get() = _loadingMovies
    private val _loadingSingleMovie = MutableLiveData(false)
    val loadingSingleMovie: LiveData<Boolean> get() = _loadingSingleMovie
    private val _movies = MutableLiveData<MutableList<Movie>>(mutableListOf())
    val movies: LiveData<MutableList<Movie>> get() = _movies
    private val _errorApiRest = MutableLiveData<String?>(null)
    val errorApiRest: LiveData<String?> get() = _errorApiRest
    private val _movieDetails = MutableLiveData(null as Movie?)
    val movieDetails: LiveData<Movie?> get() = _movieDetails
    private val _searchedMovies = MutableLiveData<MutableList<Movie>>(mutableListOf())
    val searchedMovies: LiveData<MutableList<Movie>> get() = _searchedMovies
    private val _searching = MutableLiveData(false)
    val searching: LiveData<Boolean> get() = _searching
    private val _addMovieError = MutableLiveData<String?>(null)
    val addMovieError: LiveData<String?> get() = _addMovieError
    private val _addingMovie = MutableLiveData(false)
    val addingMovie: LiveData<Boolean> get() = _addingMovie
    private val _addedMovie = MutableLiveData(false)
    val addedMovie: LiveData<Boolean> get() = _addedMovie

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _searching.value = true
            _errorApiRest.value = null

            try {
                val response = RetrofitConnectionTMDB.service.searchMovies(
                    TMDB_API_KEY, query, false, Locale.getDefault().toLanguageTag()
                )
                if (response.isSuccessful) {
                    _searchedMovies.value = response.body()?.results
                } else {
                    _errorApiRest.value = response.errorBody().toString()
                }
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            } finally {
                _searching.value = false
            }
        }
    }

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            _loadingSingleMovie.value = true
            _movieDetails.value = null
            _errorApiRest.value = null

            try {
                val response = RetrofitConnectionJSONServer.service.getMovie(id)
                if (response.isSuccessful) {
                    _loadingSingleMovie.value = false
                    _movieDetails.value = response.body()
                } else {
                    _loadingSingleMovie.value = false
                    _errorApiRest.value = response.errorBody().toString()
                }

            } catch (e: Exception) {
                _errorApiRest.value = e.message
                _loadingSingleMovie.value = false
            }
        }
    }

    fun loadMovies() {
        viewModelScope.launch {
            _loadingMovies.value = true
            _errorApiRest.value = null

            try {
                val response = RetrofitConnectionJSONServer.service.listMovies()
                if (response.isSuccessful) {
                    _movies.value = response.body()?.toMutableList()
                } else {
                    _errorApiRest.value = response.errorBody().toString()
                }

            } catch (e: Exception) {
                _errorApiRest.value = e.message
            } finally {
                _loadingMovies.value = false
            }
        }
    }

    fun onMovieUpdate(movie: Movie) {
        viewModelScope.launch {
            if (movie.id == movieDetails.value?.id) {
                getMovieDetails(movie.id!!)
            }
            try {
                RetrofitConnectionJSONServer.service.updateMovie(movie.id!!, movie)
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            } finally {
                loadMovies()
            }
        }
    }

    fun onMovieDelete(movie: Movie) {
        viewModelScope.launch {
            try {
                RetrofitConnectionJSONServer.service.deleteMovie(movie.id!!)
            } catch (e: Exception) {
                _errorApiRest.value = e.message
            } finally {
                loadMovies()
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            _addedMovie.value = false
            _addMovieError.value = null
            _addingMovie.value = true
            try {
                if (checkMovieExists(movie)) {
                    RetrofitConnectionJSONServer.service.updateMovie(movie.id!!, movie)
                } else {
                    RetrofitConnectionJSONServer.service.newMovie(movie)
                }
                _addedMovie.value = true
            } catch (e: Exception) {
                _addMovieError.value = e.message
            } finally {
                _addingMovie.value = false
                loadMovies()
            }
        }
    }

    private fun checkMovieExists(movie: Movie): Boolean {
        return movies.value?.any { it.id == movie.id } ?: false
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}
