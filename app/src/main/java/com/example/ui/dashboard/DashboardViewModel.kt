package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

import java.text.SimpleDateFormat
import java.util.Locale

import com.example.BuildConfig
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.gemini.GenerateContentRequest
import com.example.data.remote.gemini.Part

data class DashboardState(
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val sentimentDistribution: Map<String, Int> = emptyMap(),
    val dailyTendency: List<Pair<String, Float>> = emptyList(),
    val aiTips: String = "",
    val isLoadingTips: Boolean = false,
    val keywords: List<String> = emptyList(),
    val isLoadingKeywords: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val geminiApiService: GeminiApiService
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = calendar.time

        viewModelScope.launch {
            reviewRepository.getReviewsFrom(thirtyDaysAgo).collect { reviews ->
                // Basic Summary
                val total = reviews.size
                val avg = if (total > 0) reviews.map { it.rating }.average().toFloat() else 0f
                val sentiments = reviews.groupingBy { 
                    it.sentiment.ifEmpty { "Desconhecido" }
                }.eachCount()

                // Daily Trend
                val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                val groupedByDay = reviews.groupBy { dateFormat.format(it.date) }
                
                // Keep the last 30 days ordered properly. We group and calc avg.
                // We could iterate 30 days backward to make sure we have 0 for missing days,
                // but let's just use what we have, sorted by actual dates.
                // It's already sorted by date descending in the repo? The repo says orderBy date DESCENDING.
                val dailyTendencyList = groupedByDay.map { (dateStr, dayReviews) ->
                    val avgRating = dayReviews.map { it.rating }.average().toFloat()
                    dateStr to avgRating
                }.reversed() // Reverse to ascending order for chronological chart

                _dashboardState.update {
                    it.copy(
                        averageRating = avg,
                        totalReviews = total,
                        sentimentDistribution = sentiments,
                        dailyTendency = dailyTendencyList
                    )
                }

                if (_dashboardState.value.aiTips.isEmpty() && !_dashboardState.value.isLoadingTips) {
                    val lowRatingReviews = reviews.filter { it.rating <= 3f }.take(5)
                    if (lowRatingReviews.isNotEmpty()) {
                        generateAiTips(lowRatingReviews)
                    } else {
                        _dashboardState.update { it.copy(aiTips = "Ótimo trabalho! Sem avaliações baixas recentes.") }
                    }
                }

                if (_dashboardState.value.keywords.isEmpty() && !_dashboardState.value.isLoadingKeywords) {
                    val allReviewsText = reviews.take(20) // Limit to last 20 reviews for keyword analysis
                    if (allReviewsText.isNotEmpty()) {
                        generateKeywordAnalysis(allReviewsText)
                    }
                }
            }
        }
    }

    private fun generateKeywordAnalysis(reviews: List<Review>) {
        _dashboardState.update { it.copy(isLoadingKeywords = true) }
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val reviewsText = reviews.joinToString(separator = "\n") { it.reviewText }
                val prompt = "Extraia 5 a 10 termos mais relevantes ou palavras-chave das seguintes avaliações de clientes. Mantenha os termos curtos, como 'atendimento', 'comida', 'limpeza'. Responda apenas com os termos separados por vírgula, sem explicações:\n\n$reviewsText"

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = geminiApiService.generateContent(apiKey, request)
                val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val keywordList = responseText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                
                _dashboardState.update { it.copy(keywords = keywordList, isLoadingKeywords = false) }
            } catch (e: Exception) {
                _dashboardState.update { it.copy(isLoadingKeywords = false) }
            }
        }
    }

    private fun generateAiTips(lowReviews: List<Review>) {
        _dashboardState.update { it.copy(isLoadingTips = true) }
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val reviewsText = lowReviews.joinToString(separator = "\n") {
                    "Nota: ${it.rating}, Comentário: ${it.reviewText}"
                }
                val prompt = "Você é um especialista em melhoria de atendimento ao consumidor. Com base nas seguintes avaliações baixas recebidas:\n\n$reviewsText\n\nListe 3 dicas práticas em tópicos curtos para a equipe melhorar o serviço. Responda em português."

                val request = GenerateContentRequest(
                    contents = listOf(Content(
                        parts = listOf(Part(text = prompt))
                    ))
                )
                val response = geminiApiService.generateContent(apiKey, request)
                val tips = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Nenhuma dica disponível."
                
                _dashboardState.update { it.copy(aiTips = tips, isLoadingTips = false) }
            } catch (e: Exception) {
                _dashboardState.update { 
                    it.copy(
                        aiTips = "Erro ao buscar dicas: ${e.message}",
                        isLoadingTips = false
                    ) 
                }
            }
        }
    }

    fun simulateNegativeReview() {
        viewModelScope.launch {
            val fakeReview = com.example.domain.model.Review(
                author = "Cliente Simulado",
                rating = 1f,
                reviewText = "Simulação: O pedido chegou atrasado e não era o que eu pedi. Estou muito decepcionado e quero meu dinheiro de volta.",
                sentiment = "negativo"
            )
            reviewRepository.addReview(fakeReview)
        }
    }
}
