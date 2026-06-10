package com.example.domain.repository

import com.example.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun addReview(review: Review): Result<Unit>
    fun getReviews(): Flow<List<Review>>
    fun getReviewsFilteredByRating(minRating: Float): Flow<List<Review>>
    fun getNewLowRatingReviews(): Flow<Review>
    fun getReviewsFrom(date: java.util.Date): Flow<List<Review>>
}
