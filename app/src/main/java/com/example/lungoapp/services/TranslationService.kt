package com.example.lungoapp.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationService {
    @GET("translate")
    suspend fun translateWord(
        @Query("text") text: String,
        @Query("source") source: String = "en",
        @Query("target") target: String = "tr"
    ): Response<TranslationResponse>
}

data class TranslationResponse(
    val original_text: String,
    val translated_text: String,
    val source_language: String,
    val target_language: String
) 