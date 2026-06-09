package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_profiles")
data class BusinessProfile(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val address: String,
    val phone: String,
    val website: String,
    val hours: String,
    val description: String,
    val photosCount: Int,
    val rating: Double,
    val reviewsCount: Int,
    val reviewsAnsweredCount: Int,
    val postsRecentCount: Int,
    val hasCompletenessError: Boolean,
    val diagnosticScore: Int,
    val lastDiagnosticTime: Long = System.currentTimeMillis()
) {
    val reviewsAnsweredPercent: Int
        get() = if (reviewsCount > 0) (reviewsAnsweredCount * 100) / reviewsCount else 100
}

@Serializable
@Entity(tableName = "business_reviews")
data class Review(
    @PrimaryKey val id: String,
    val businessId: String,
    val authorName: String,
    val authorPhotoUrl: String?,
    val rating: Int,
    val text: String,
    val publishTime: Long,
    val responseText: String?,
    val aiSuggestedResponse: String?,
    val isResponsePending: Boolean = responseText.isNullOrBlank()
)

@Serializable
@Entity(tableName = "post_suggestions")
data class PostSuggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val businessId: String,
    val title: String,
    val content: String,
    val category: String, // ex: "Oferta", "Novidade", "Evento"
    val isSaved: Boolean = false,
    val createdTime: Long = System.currentTimeMillis()
)

@Serializable
data class DiagnosticResult(
    val score: Int,
    val correctList: List<String>,
    val warningList: List<String>,
    val improvementSuggestions: List<ImprovementCategory>,
    val optimizedDescription: String
)

@Serializable
data class ImprovementCategory(
    val priority: String, // ex: "Alta", "Médiia", "Baixa"
    val title: String,
    val description: String,
    val actionLabel: String
)

@Serializable
data class AcademyArticle(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Como Iniciar", "SEO Local", "Avaliações", "Posts Otimizados"
    val contentMarkdown: String,
    val isRead: Boolean = false
)
