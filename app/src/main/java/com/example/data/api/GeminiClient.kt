package com.example.data.api

import com.example.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

// --- Gemini Content Structures ---

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 1000
)

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
    private val gson = Gson()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API generating a response for the given prompt.
     */
    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY") {
            return@withContext "Gemini API key is not configured. Please add your key in the Secrets panel in AI Studio."
        }

        val requestUrl = "$BASE_URL?key=$apiKey"

        val contents = listOf(Content(parts = listOf(Part(text = prompt))))
        val sysInstructionContent = systemInstruction?.let {
            Content(parts = listOf(Part(text = it)))
        }

        val geminiReq = GeminiRequest(
            contents = contents,
            systemInstruction = sysInstructionContent,
            generationConfig = GenerationConfig()
        )

        val jsonBody = gson.toJson(geminiReq)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    return@withContext "API Call Failed: Code ${response.code}\n$errBody"
                }

                val resBodyStr = response.body?.string() ?: return@withContext "Error: Received empty response"
                val responseObj = gson.fromJson(resBodyStr, GeminiResponse::class.java)

                return@withContext responseObj?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Could not interpret Gemini response."
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext "Network Error: ${e.message}. Please check your connection."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "An unexpected error occurred: ${e.message}"
        }
    }
}
