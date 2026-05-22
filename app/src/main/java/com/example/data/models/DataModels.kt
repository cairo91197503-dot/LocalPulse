package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorPhotoUrl: String?,
    val rating: Int,
    val comment: String,
    val createTime: String,
    val replyText: String? = null,
    val isReplied: Boolean = false,
    val isOfflinePending: Boolean = false
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val createTime: String,
    val imageUrl: String? = null
)

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: String = "default_business",
    val name: String,
    val rating: Float,
    val reviewCount: Int,
    val unrepliedCount: Int,
    val lastPostDate: String? = null
)

@Entity(tableName = "sentiment_summary")
data class SentimentSummary(
    @PrimaryKey val id: String = "weekly_summary",
    val text: String,
    val dateUpdated: String
)
