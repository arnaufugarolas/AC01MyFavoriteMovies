package com.arnaufugarolas.ac01myfavoritemovies.server

import com.arnaufugarolas.ac01myfavoritemovies.dataClass.WeatherResponse
import retrofit2.Response
import retrofit2.http.*


interface RetrofitEndPointsWeather {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") key: String,
        @Query("q") q: String,
        @Query("aqi") aqi: String,
        @Query("lang") lang: String
    ): Response<WeatherResponse>
}

