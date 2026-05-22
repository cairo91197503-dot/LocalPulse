package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Review
import com.example.data.models.Post
import com.example.data.models.BusinessProfile
import com.example.data.models.SentimentSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalPulseDao {
    // Reviews queries
    @Query("SELECT * FROM reviews ORDER BY createTime DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE rating >= 4 ORDER BY createTime DESC")
    fun getPositiveReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE rating <= 3 ORDER BY createTime DESC")
    fun getNegativeReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE isReplied = 0 ORDER BY createTime DESC")
    fun getUnrepliedReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :id LIMIT 1")
    suspend fun getReviewById(id: String): Review?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Update
    suspend fun updateReview(review: Review)

    @Query("DELETE FROM reviews")
    suspend fun clearReviews()

    // Posts queries
    @Query("SELECT * FROM posts ORDER BY createTime DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    // Business Profile queries
    @Query("SELECT * FROM business_profile WHERE id = :id LIMIT 1")
    fun getBusinessProfile(id: String = "default_business"): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBusinessProfile(profile: BusinessProfile)

    // Sentiment Summary queries
    @Query("SELECT * FROM sentiment_summary WHERE id = :id LIMIT 1")
    fun getSentimentSummary(id: String = "weekly_summary"): Flow<SentimentSummary?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSentimentSummary(summary: SentimentSummary)
}
