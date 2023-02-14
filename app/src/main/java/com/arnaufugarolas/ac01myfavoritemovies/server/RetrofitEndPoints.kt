package com.arnaufugarolas.ac01myfavoritemovies.server

import com.arnaufugarolas.ac01myfavoritemovies.Movie
import retrofit2.Response
import retrofit2.http.*

interface RetrofitEndPoints {
    @GET("movies")
    suspend fun listMovies(): Response<List<Movie>>

    @POST("movies")
    suspend fun newMovie(@Body movie: Movie?)

    @PUT("movies/{id}")
    suspend fun updateMovie(@Path("id") id: Int, @Body movie: Movie?)

    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Int)
}
