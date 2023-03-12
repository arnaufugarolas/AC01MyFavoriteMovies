package com.arnaufugarolas.ac01myfavoritemovies.server

import com.arnaufugarolas.ac01myfavoritemovies.Secrets.JSON_SERVER_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitConnectionJSONServer {
    private val okHttpClient = HttpLoggingInterceptor().run {
        level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(this).build()
    }

    private val builder = Retrofit.Builder()
        .baseUrl(JSON_SERVER_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: RetrofitEndPointsJSONServer = builder.create()
}
