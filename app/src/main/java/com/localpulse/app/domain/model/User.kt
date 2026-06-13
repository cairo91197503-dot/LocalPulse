package com.localpulse.app.domain.model

/**
 * Domain model representing an authenticated user.
 *
 * @property uid Unique identifier from Firebase.
 * @property name User's display name.
 * @property email User's email address.
 * @property photoUrl URL for the user's profile picture, if available.
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val plan: String = "free",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)
