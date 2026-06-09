package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class Business(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Restaurant, Retail, Services, Health, Leisure
    val description: String,
    val rating: Float,
    val address: String,
    val contact: String,
    val recommendedCount: Int = 0
)

@Entity(tableName = "academies")
data class Academy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Technology, Language, Arts, Business, Cooking
    val description: String,
    val coursesCount: Int,
    val address: String,
    val contact: String,
    val certificateInfo: String
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val academyId: Int,
    val title: String,
    val level: String, // Beginner, Intermediate, Advanced
    val duration: String, // e.g. "6 weeks", "3 months"
    val syllabusOverview: String,
    val userEnrolled: Boolean = false
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetId: Int,
    val targetType: String, // "business" or "academy"
    val userEmail: String,
    val reviewText: String,
    val rating: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "enrollments")
data class Enrollment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val academyId: Int,
    val courseTitle: String,
    val userEmail: String,
    val status: String, // "Enrolled", "Completed"
    val studyPathSuggestion: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
