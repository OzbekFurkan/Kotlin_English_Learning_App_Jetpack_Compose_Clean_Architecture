package com.example.lungoapp.data.repository

import com.example.lungoapp.data.remote.WordApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordApi: WordApi
) : WordRepository {

    override suspend fun getRandomWord(): String {
        val response = wordApi.getRandomWord()
        if (!response.isSuccessful) {
            throw Exception("Failed to get random word")
        }
        return response.body()?.word ?: throw Exception("Word not found")
    }

    override suspend fun getRandomWords(count: Int): List<String> {
        val response = wordApi.getRandomWords(count)
        if (!response.isSuccessful) {
            throw Exception("Failed to get random words")
        }
        return response.body()?.map { it.word } ?: throw Exception("Words not found")
    }

    override suspend fun addWord(word: String) {
        // Not implemented as we're not storing words locally anymore
    }

    override suspend fun removeWord(word: String) {
        // Not implemented as we're not storing words locally anymore
    }

    override suspend fun getAllWords(): List<String> {
        // Not implemented as we're not storing words locally anymore
        return emptyList()
    }
} 