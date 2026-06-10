package com.example.domain.service

interface AiResponseService {
    suspend fun generateSuggestedResponse(rating: Float, sentiment: String, reviewText: String): Result<String>
}
