package com.example.ui.reviews.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.gemini.GenerateContentRequest
import com.example.data.remote.gemini.Part
import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewDetailState(
    val review: Review? = null,
    val draftResponse: String = "",
    val isDraftGenerating: Boolean = false,
    val draftError: String? = null
)

@HiltViewModel
class ReviewDetailViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val geminiApiService: GeminiApiService
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewDetailState())
    val state: StateFlow<ReviewDetailState> = _state.asStateFlow()

    fun loadReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.getReviews().collect { reviews ->
                val review = reviews.find { it.id == reviewId }
                if (review != null) {
                    _state.update { it.copy(review = review) }
                    generateDraftResponse(review)
                }
            }
        }
    }

    private fun generateDraftResponse(review: Review) {
        // Only generate draft if it doesn't have one and we aren't generating already
        if (_state.value.draftResponse.isNotEmpty() || _state.value.isDraftGenerating) return

        _state.update { it.copy(isDraftGenerating = true, draftError = null) }
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val prompt = """
                    Você é um assistente de atendimento ao cliente para um estabelecimento comercial.
                    Abaixo está uma avaliação recebida de um cliente.
                    Crie uma resposta profissional, educada e empática para esta avaliação.
                    A resposta deve ser em português. Não inclua os cabeçalhos como "Resposta sugerida:", apenas o corpo do texto.
                    
                    Nota: ${review.rating} / 5
                    Comentário: ${review.reviewText}
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = geminiApiService.generateContent(apiKey, request)
                val draft = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Não foi possível gerar um rascunho."
                
                _state.update { it.copy(isDraftGenerating = false, draftResponse = draft) }
            } catch (e: Exception) {
                 _state.update { it.copy(isDraftGenerating = false, draftError = "Erro ao gerar rascunho: ${e.message}") }
            }
        }
    }
}
