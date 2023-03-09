package com.arnaufugarolas.ac01myfavoritemovies.dataClass

import com.arnaufugarolas.ac01myfavoritemovies.enumClass.DifferenceType

data class Difference<Movie>(
    val type: DifferenceType,
    val oldItem: Movie?,
    val newItem: Movie?,
    val newIndex: Int?
)
