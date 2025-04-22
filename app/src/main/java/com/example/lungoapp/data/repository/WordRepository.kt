package com.example.lungoapp.data.repository

interface WordRepository {
    suspend fun getRandomWord(): String
    suspend fun getRandomWords(count: Int): List<String>
    suspend fun addWord(word: String)
    suspend fun removeWord(word: String)
    suspend fun getAllWords(): List<String>
} 