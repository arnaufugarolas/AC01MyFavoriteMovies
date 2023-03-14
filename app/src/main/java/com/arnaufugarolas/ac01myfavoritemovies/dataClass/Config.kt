package com.arnaufugarolas.ac01myfavoritemovies.dataClass

import com.google.gson.annotations.SerializedName


data class Config(
    @SerializedName("id") var id: String? = null,
    @SerializedName("city") var city: String? = null
)
