package com.arnaufugarolas.ac01myfavoritemovies.dataClass

import com.google.gson.annotations.SerializedName


data class SearchResponse(
    @SerializedName("page") var page: Int,
    @SerializedName("results") var results: MutableList<Movie>,
    @SerializedName("total_pages") var totalPages: Int,
    @SerializedName("total_results") var totalResults: Int
)
