package com.arnaufugarolas.ac01myfavoritemovies.server

import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Config
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.Movie
import retrofit2.Response
import retrofit2.http.*

interface RetrofitEndPointsJSONServer {
    @GET("movies")
    suspend fun listMovies(): Response<List<Movie>>

    @GET("movies/{id}")
    suspend fun getMovie(@Path("id") id: Int): Response<Movie>

    @POST("movies")
    suspend fun newMovie(@Body movie: Movie?)

    @PUT("movies/{id}")
    suspend fun updateMovie(@Path("id") id: Int, @Body movie: Movie?)

    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Int)

    @GET("conf/city")
    suspend fun getCity(): Response<Config>

    @PUT("conf/city")
    suspend fun updateCity(@Body config: Config): Response<Config>
}
