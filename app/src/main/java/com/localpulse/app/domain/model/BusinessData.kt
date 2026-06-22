package com.localpulse.app.domain.model

/**
 * Dados do negócio informados pelo usuário para diagnóstico.
 */
data class BusinessData(
    val businessLink: String? = null,
    val businessName: String = "",
    val category: String = "",
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val respondedPercentage: Int = 0,
    val hasProfilePhoto: Boolean = false,
    val hasHours: Boolean = false,
    val hasDescription: Boolean = false,
    val hasWebsite: Boolean = false
)
