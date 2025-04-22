package com.example.lungoapp.services

import android.util.Log
import com.example.lungoapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {
    private val tag = "GeminiService"
    private var model: GenerativeModel? = null

    init {
        try {
            model = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
            Log.d(tag, "GeminiService initialized successfully")
        } catch (e: Exception) {
            Log.e(tag, "Error initializing GeminiService", e)
        }
    }

    suspend fun generateText(prompt: String): String {
        try {
            if (model == null) {
                throw Exception("GeminiService not properly initialized")
            }

            val response = model?.generateContent(prompt)
                ?: throw Exception("Failed to generate content: model is null")

            return response.text?.trim() ?: throw Exception("Generated content is empty")
        } catch (e: Exception) {
            Log.e(tag, "Error generating text", e)
            throw e
        }
    }

    suspend fun generateSnippet(level: String): String {
        try {
            if (model == null) {
                throw Exception("GeminiService not properly initialized")
            }

            val prompt = """
                Generate a short, natural English sentence (15-20 words) suitable for an English ${level} level student.
                The sentence should be conversational and include common vocabulary.
                Only return the sentence, nothing else.
            """.trimIndent()

            val response = model?.generateContent(prompt)
                ?: throw Exception("Failed to generate content: model is null")

            return response.text?.trim() ?: throw Exception("Generated content is empty")
        } catch (e: Exception) {
            Log.e(tag, "Error generating snippet", e)
            throw e
        }
    }
} 