package com.localpulse.app.data.ai

import com.localpulse.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReviewAssistantRepository @Inject constructor() {

    suspend fun generateResponseSuggestion(customerReview: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
                return@withContext Result.failure(Exception("API Key not configured. Please add it to AI Studio Secrets."))
            }

            val prompt = """
                Você é um assistente especialista em atendimento ao cliente para pequenos negócios locais.
                Um cliente deixou a seguinte avaliação negativa:
                "$customerReview"
                
                Escreva 3 opções de respostas profissionais, empáticas e resolutivas para este cliente. As respostas devem ser curtas e diretas.
                Formate a resposta retornando APENAS as 3 opções em tópicos claros.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.7f
                )
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (text != null) {
                Result.success(text)
            } else {
                Result.failure(Exception("Could not generate a response."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
