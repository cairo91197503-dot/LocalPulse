package com.example.data.repository

import com.example.data.api.GeminiClient
import com.example.data.db.BusinessDao
import com.example.data.db.ReviewDao
import com.example.data.models.Business
import com.example.data.models.Review
import kotlinx.coroutines.flow.Flow

class BusinessRepository(
    private val businessDao: BusinessDao,
    private val reviewDao: ReviewDao
) {
    val allBusinesses: Flow<List<Business>> = businessDao.getAllBusinesses()

    fun getBusinessesByCategory(category: String): Flow<List<Business>> {
        return businessDao.getBusinessesByCategory(category)
    }

    suspend fun getBusinessById(id: Int): Business? {
        return businessDao.getBusinessById(id)
    }

    suspend fun insertBusiness(business: Business): Long {
        return businessDao.insertBusiness(business)
    }

    suspend fun recommendBusiness(id: Int) {
        businessDao.incrementRecommendCount(id)
    }

    fun getReviewsForBusiness(businessId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsForTarget(businessId, "business")
    }

    suspend fun insertReview(review: Review): Long {
        return reviewDao.insertReview(review)
    }

    /**
     * Use Gemini to provide customized business matches based on list of actual local businesses.
     */
    suspend fun getAiBusinessRecommendations(userPreference: String, actualBusinesses: List<Business>): String {
        if (actualBusinesses.isEmpty()) {
            return "No businesses available to recommend."
        }

        val bizContext = actualBusinesses.joinToString("\n") {
            "- ${it.name} (${it.category}) at ${it.address}. Desc: ${it.description}. Rating: ${it.rating}★, Recommended: ${it.recommendedCount} times."
        }

        val prompt = """
            The user is searching for something in our local city. Here is their preference or question:
            "$userPreference"

            Here are the actual registered businesses currently in our LocalPulse directories:
            $bizContext

            Please analyze the user's preference and recommend the MOST relevant business or businesses from the list above. 
            Give a lively, friendly local recommendation that highlights why it is a great match. 
            If none of the businesses matches perfectly, recommend the closest useful option and suggest how they could check it out anyway.
            Keep your recommendation short (2-3 paragraphs) and conversational.
        """.trimIndent()

        val systemInstruction = """
            You are 'LocalPulse Assistant', a lively, knowledgeable, and enthusiastic AI guide for our local community. 
            You are passionate about helping users find the best spots, hidden gems, and community businesses.
            Always maintain a helpful, warm, neighborly tone.
        """.trimIndent()

        return GeminiClient.generateResponse(prompt, systemInstruction)
    }
}
