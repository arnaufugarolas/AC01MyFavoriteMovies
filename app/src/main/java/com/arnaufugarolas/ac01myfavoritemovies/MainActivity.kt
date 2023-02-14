package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val json = """
        {
          "adult": false,
          "backdrop_path": "https://image.tmdb.org/t/p/original/faXT8V80JRhnArTAeYXz0Eutpv9.jpg",
          "favorite": true,
          "genre_ids": [
            16,
            28,
            12,
            35,
            10751,
            14
          ],
          "id": 315162,
          "original_language": "en",
          "original_title": "Puss in Boots: The Last Wish",
          "overview": "Puss in Boots discovers that his passion for adventure has taken its toll: He has burned through eight of his nine lives, leaving him with only one life left. Puss sets out on an epic journey to find the mythical Last Wish and restore his nine lives.",
          "popularity": 6660.227,
          "poster_path": "https://image.tmdb.org/t/p/original/kuf6dutpsT0vSVehic3EZIqkOBt.jpg",
          "release_date": "2022-12-07",
          "title": "Puss in Boots: The Last Wish",
          "video": false,
          "vote_average": 8.6,
          "vote_count": 2814,
          "my_score": 9
        }
    """
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupVariables()
    }

    private fun setupVariables() {
        gson = Gson()
        val jsonMovie = gson.fromJson(json, Movie::class.java)
        Log.d("Buenas", "Buenas")
    }
}
