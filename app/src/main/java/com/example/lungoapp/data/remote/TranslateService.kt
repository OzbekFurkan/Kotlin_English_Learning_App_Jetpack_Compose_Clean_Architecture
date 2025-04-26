package com.example.lungoapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class TranslationResponse(
    @SerializedName("original_text")
    val originalText: String,
    @SerializedName("translated_text")
    val translatedText: String,
    @SerializedName("source_language")
    val sourceLanguage: String,
    @SerializedName("target_language")
    val targetLanguage: String
)

interface TranslateService {
    @GET("translate")
    suspend fun translate(
        @Query("text") text: String,
        @Query("target") targetLanguage: String
    ): retrofit2.Response<TranslationResponse>
} 