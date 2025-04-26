package com.example.lungoapp.data.repository

import android.util.Log
import com.example.lungoapp.data.model.Bookmark
import com.example.lungoapp.data.remote.ApiService
import com.example.lungoapp.data.remote.TranslateService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val apiService: ApiService,
    private val translateService: TranslateService
) {
    suspend fun saveBookmark(word: String): Bookmark {
        try {
            // Get translation first
            val translationResponse = translateService.translate(word, "tr")
            Log.d("BookmarkRepository", "Translation response: $translationResponse")
            
            if (!translationResponse.isSuccessful) {
                Log.e("BookmarkRepository", "Translation failed: ${translationResponse.code()}")
                throw Exception("Translation failed: ${translationResponse.code()}")
            }
            
            val translation = translationResponse.body()?.translatedText
            Log.d("BookmarkRepository", "Word: $word, Translation: $translation")
            
            if (translation.isNullOrEmpty()) {
                Log.e("BookmarkRepository", "Translation is null or empty")
                throw Exception("Translation is null or empty")
            }
            
            return apiService.createBookmark(mapOf(
                "word" to word,
                "word_tr" to translation
            ))
        } catch (e: Exception) {
            Log.e("BookmarkRepository", "Error saving bookmark: ${e.message}")
            throw e
        }
    }

    suspend fun getBookmarks(): List<Bookmark> {
        return apiService.getBookmarks()
    }

    suspend fun deleteBookmark(bookmarkId: Int) {
        apiService.deleteBookmark(bookmarkId)
    }
} 