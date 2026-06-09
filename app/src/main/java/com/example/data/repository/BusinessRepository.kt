package com.example.data.repository

import com.example.data.api.GeminiClient
import com.example.data.db.BusinessDao
import com.example.data.models.BusinessProfile
import com.example.data.models.DiagnosticResult
import com.example.data.models.PostSuggestion
import com.example.data.models.Review
import kotlinx.coroutines.flow.Flow

class BusinessRepository(private val businessDao: BusinessDao) {

    // --- Profile Operations ---
    
    fun getAllProfiles(): Flow<List<BusinessProfile>> = businessDao.getAllProfiles()

    fun getProfileById(id: String): Flow<BusinessProfile?> = businessDao.getProfileById(id)

    suspend fun insertProfile(profile: BusinessProfile) = businessDao.insertProfile(profile)

    suspend fun insertProfiles(profiles: List<BusinessProfile>) = businessDao.insertProfiles(profiles)

    suspend fun deleteProfile(id: String) = businessDao.deleteProfile(id)

    // --- Review Operations ---

    fun getReviewsForBusiness(businessId: String): Flow<List<Review>> = businessDao.getReviewsForBusiness(businessId)

    suspend fun updateReview(review: Review) = businessDao.updateReview(review)

    suspend fun insertReviews(reviews: List<Review>) = businessDao.insertReviews(reviews)

    // --- Post Suggestion Operations ---

    fun getPostSuggestions(businessId: String): Flow<List<PostSuggestion>> = businessDao.getPostSuggestions(businessId)

    suspend fun insertPostSuggestion(suggestion: PostSuggestion) = businessDao.insertPostSuggestion(suggestion)

    suspend fun updatePostSuggestion(suggestion: PostSuggestion) = businessDao.updatePostSuggestion(suggestion)

    suspend fun deletePostSuggestion(id: Int) = businessDao.deletePostSuggestion(id)

    // --- AI/Gemini Operations ---

    suspend fun analyzeProfile(profile: BusinessProfile): DiagnosticResult {
        val result = GeminiClient.analyzeBusinessProfile(profile)
        // Optionally cache details in DB
        val updatedProfile = profile.copy(
            diagnosticScore = result.score,
            lastDiagnosticTime = System.currentTimeMillis()
        )
        businessDao.insertProfile(updatedProfile)
        return result
    }

    suspend fun generateReviewAnswer(businessName: String, review: Review): String {
        val reply = GeminiClient.generateReviewReply(businessName, review.text, review.rating)
        val updatedReview = review.copy(aiSuggestedResponse = reply)
        businessDao.updateReview(updatedReview)
        return reply
    }

    suspend fun generatePostDraft(businessId: String, businessName: String, category: String, theme: String): PostSuggestion {
        val (title, content) = GeminiClient.generatePostSuggestion(businessName, category, theme)
        val suggestion = PostSuggestion(
            businessId = businessId,
            title = title,
            content = content,
            category = category
        )
        businessDao.insertPostSuggestion(suggestion)
        return suggestion
    }
}
