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
    private val _moviesCount = MutableLiveData(0)
    val moviesCount: LiveData<Int> get() = _moviesCount
    private val _movies = MutableLiveData<MutableList<Movie>>(emptyList())
    val movies: LiveData<MutableList<Movie>> get() = _movies
    private val _errorApiRest = MutableLiveData<String?>(null)
    val errorApiRest: LiveData<String?> get() = _errorApiRest

    val tmdbApiKey: String = "dfe8494cd7c331306cfcc31e6fdeedc4"
    val weatherApiKey: String = "7f7390123a564931910160705231002"

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null

            try {
                val response = RetrofitConnection.service.listMovies()

                if (response.isSuccessful) {
                    _movies.value = response.body()?.toMutableList()
                    _moviesCount.value = _movies.value!!.size
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

    fun onMovieClicked(movie: Movie) {
        movie.favorite = movie.favorite != true

        viewModelScope.launch {
            RetrofitConnection.service.updateMovie(movie.id!!, movie)
            loadMovies()
        }
    }

    fun onMovieDelete(movie: Movie) {
        viewModelScope.launch {
            RetrofitConnection.service.deleteMovie(movie.id!!)
        }
    }

    /*
        fun newMovieAI(iYear: Int) {
            viewModelScope.launch {
                var prompt: String = "suggest json object with a movie from year $iYear with name, director and poster"
                try {
                    _loading.value = true
                    _errorApiRest.value = null
                    val response = openAI.createCompletion(
                        model = "text-davinci-003",
                        prompt = prompt,
                        temperature = 0.9,
                        max_tokens = 150,
                        top_p = 1,
                        frequency_penalty = 0.0,
                        presence_penalty = 0.6,
                        stop = listOf(" Human:", " AI:")
                    )

                    if (response.isSuccessful) {
                        var answer = response.body()?.choices?.first()?.text
                        Log.d("OPENAI",answer!!)
                        answer = answer!!.replace("\n","")

                        // convertimos al modelo que sabemos que devolverá
                        val movieAI: MovieAI = Gson().fromJson(answer, MovieAI::class.java)


                        var movie = Movie(movieAI.name)
                        movie.posterPath = movieAI.poster

                        // creamos
                        RetrofitConnection.service.newMovie(movie)

                        // recargamos la lista que el observable de la activity recargará.
                        loadMovies()
                    } else {
                        Log.d("RESPONSE", "Error: ${response.code()} ${response.message()}")
                    }
                    _loading.value = false
                } catch (e: Exception) {
                    _loading.value = false
                    _errorApiRest.value = e.toString()
                    Log.d("RESPONSE", "Error: $e")
                }
            }
        }
    */
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}
