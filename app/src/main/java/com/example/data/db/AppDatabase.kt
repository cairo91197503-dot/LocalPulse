package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.models.BusinessProfile
import com.example.data.models.Review
import com.example.data.models.PostSuggestion
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM business_profiles")
    fun getAllProfiles(): Flow<List<BusinessProfile>>

    @Query("SELECT * FROM business_profiles WHERE id = :id")
    fun getProfileById(id: String): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BusinessProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<BusinessProfile>)

    @Query("DELETE FROM business_profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)

    // Reviews
    @Query("SELECT * FROM business_reviews WHERE businessId = :businessId ORDER BY publishTime DESC")
    fun getReviewsForBusiness(businessId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    @Update
    suspend fun updateReview(review: Review)

    // Post suggestions
    @Query("SELECT * FROM post_suggestions WHERE businessId = :businessId ORDER BY createdTime DESC")
    fun getPostSuggestions(businessId: String): Flow<List<PostSuggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostSuggestion(suggestion: PostSuggestion)

    @Update
    suspend fun updatePostSuggestion(suggestion: PostSuggestion)

    @Query("DELETE FROM post_suggestions WHERE id = :id")
    suspend fun deletePostSuggestion(id: Int)
}

@Database(entities = [BusinessProfile::class, Review::class, PostSuggestion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localpulse_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
