package com.arnaufugarolas.ac01myfavoritemovies.server

import com.arnaufugarolas.ac01myfavoritemovies.dataClass.SearchResponse
import retrofit2.Response
import retrofit2.http.*


interface RetrofitEndPointsTMDB {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("language") language: String
    ): Response<SearchResponse>
}

