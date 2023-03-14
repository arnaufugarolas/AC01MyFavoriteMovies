package com.arnaufugarolas.ac01myfavoritemovies.dataClass

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    @SerializedName("location") var location: Location,
    @SerializedName("current") var current: Current
)
