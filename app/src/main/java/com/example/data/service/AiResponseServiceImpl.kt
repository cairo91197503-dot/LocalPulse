package com.example.data.service

import com.example.BuildConfig
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.GenerateContentRequest
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.gemini.Part
import com.example.domain.service.AiResponseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AiResponseServiceImpl @Inject constructor(
    private val geminiApiService: GeminiApiService
) : AiResponseService {

    override suspend fun generateSuggestedResponse(
        rating: Float,
        sentiment: String,
        reviewText: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("API Key do Gemini não configurada."))
        }

        val prompt = """
            Você é um assistente virtual para donos de pequenos negócios.
            Um cliente deixou a seguinte avaliação para o negócio:
            
            Nota: ${rating} estrelas de 5
            Sentimento identificado: $sentiment
            Texto da avaliação: "$reviewText"
            
            Gere uma resposta profissional, educada e empática para esta avaliação.
            A resposta deve agradecer o feedback e, se a nota for baixa ou o sentimento negativo, oferecer suporte ou pedir desculpas pela má experiência, de maneira concisa.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )

        try {
            val response = geminiApiService.generateContent(apiKey, request)
            val suggestedText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (suggestedText != null) {
                Result.success(suggestedText)
            } else {
                Result.failure(Exception("Não foi possível gerar a resposta."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
