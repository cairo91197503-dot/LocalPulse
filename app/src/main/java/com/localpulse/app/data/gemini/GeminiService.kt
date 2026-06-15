package com.localpulse.app.data.gemini

import com.localpulse.app.BuildConfig
import com.localpulse.app.domain.model.BusinessData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço de integração com a API Gemini para diagnóstico de reputação.
 */
@Singleton
class GeminiService @Inject constructor() {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

    /**
     * Gera diagnóstico de reputação baseado nos dados do negócio.
     */
    suspend fun generateDiagnosis(businessData: BusinessData): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(businessData)
                val requestBody = buildRequestBody(prompt)

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                connection.outputStream.use { os ->
                    os.write(requestBody.toByteArray())
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val text = parseResponse(response)
                    Result.success(text)
                } else {
                    val error = connection.errorStream?.bufferedReader()?.readText() ?: "Erro desconhecido"
                    Result.failure(Exception("Erro $responseCode: $error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun buildPrompt(data: BusinessData): String {
        return """
            Você é um especialista em reputação online para pequenos negócios no Brasil.
            
            Analise os dados do negócio abaixo e gere um diagnóstico de reputação completo em português.
            
            DADOS DO NEGÓCIO:
            - Nome: ${data.businessName}
            - Categoria: ${data.category}
            - Nota média no Google: ${data.averageRating}/5
            - Total de avaliações: ${data.totalReviews}
            - % de avaliações respondidas: ${data.respondedPercentage}%
            - Tem foto de perfil: ${if (data.hasProfilePhoto) "Sim" else "Não"}
            - Tem horário cadastrado: ${if (data.hasHours) "Sim" else "Não"}
            - Tem descrição: ${if (data.hasDescription) "Sim" else "Não"}
            - Tem site vinculado: ${if (data.hasWebsite) "Sim" else "Não"}
            
            Responda EXATAMENTE neste formato JSON (sem markdown, sem explicações extras):
            {
              "score": [número de 0 a 100],
              "nivel": "[Crítico/Baixo/Moderado/Bom/Excelente]",
              "resumo": "[2 frases resumindo a situação]",
              "pontos_positivos": ["item1", "item2"],
              "pontos_negativos": ["item1", "item2"],
              "acoes_prioritarias": [
                {"titulo": "ação", "descricao": "como fazer", "impacto": "Alto/Médio/Baixo"},
                {"titulo": "ação", "descricao": "como fazer", "impacto": "Alto/Médio/Baixo"},
                {"titulo": "ação", "descricao": "como fazer", "impacto": "Alto/Médio/Baixo"}
              ]
            }
        """.trimIndent()
    }

    private fun buildRequestBody(prompt: String): String {
        val jsonBody = JSONObject()
        val contents = JSONArray()
        val content = JSONObject()
        val parts = JSONArray()
        val part = JSONObject()

        part.put("text", prompt)
        parts.put(part)
        content.put("parts", parts)
        contents.put(content)
        jsonBody.put("contents", contents)

        return jsonBody.toString()
    }

    private fun parseResponse(response: String): String {
        val json = JSONObject(response)
        return json
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }
}
