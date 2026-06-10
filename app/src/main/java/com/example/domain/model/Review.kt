package com.example.domain.model

import java.util.Date

data class Review(
    val id: String = "",
    val author: String = "",
    val rating: Float = 0f,
    val reviewText: String = "",
    val date: Date = Date(),
    val sentiment: String = ""
)
