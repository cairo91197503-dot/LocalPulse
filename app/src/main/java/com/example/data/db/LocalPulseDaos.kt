package com.example.data.db

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY rating DESC")
    fun getAllBusinesses(): Flow<List<Business>>

    @Query("SELECT * FROM businesses WHERE category = :category ORDER BY rating DESC")
    fun getBusinessesByCategory(category: String): Flow<List<Business>>

    @Query("SELECT * FROM businesses WHERE id = :id")
    suspend fun getBusinessById(id: Int): Business?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business): Long

    @Query("UPDATE businesses SET recommendedCount = recommendedCount + 1 WHERE id = :id")
    suspend fun incrementRecommendCount(id: Int)
}

@Dao
interface AcademyDao {
    @Query("SELECT * FROM academies ORDER BY name ASC")
    fun getAllAcademies(): Flow<List<Academy>>

    @Query("SELECT * FROM academies WHERE category = :category ORDER BY name ASC")
    fun getAcademiesByCategory(category: String): Flow<List<Academy>>

    @Query("SELECT * FROM academies WHERE id = :id")
    suspend fun getAcademyById(id: Int): Academy?

    @Query("SELECT * FROM courses WHERE academyId = :academyId")
    fun getCoursesByAcademyId(academyId: Int): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcademy(academy: Academy): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Query("UPDATE courses SET userEnrolled = :enrolled WHERE id = :id")
    suspend fun setCourseEnrollmentStatus(id: Int, enrolled: Boolean)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE targetId = :targetId AND targetType = :targetType ORDER BY timestamp DESC")
    fun getReviewsForTarget(targetId: Int, targetType: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long
}

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM enrollments ORDER BY timestamp DESC")
    fun getAllEnrollments(): Flow<List<Enrollment>>

    @Query("SELECT * FROM enrollments WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getEnrollmentsByEmail(email: String): Flow<List<Enrollment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: Enrollment): Long

    @Delete
    suspend fun deleteEnrollment(enrollment: Enrollment)
}
