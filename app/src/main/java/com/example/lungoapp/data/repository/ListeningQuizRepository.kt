package com.example.lungoapp.data.repository

import com.example.lungoapp.data.model.ListeningQuiz
import com.example.lungoapp.services.GeminiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListeningQuizRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val wordRepository: WordRepository
) {
    suspend fun generateQuiz(level: String): ListeningQuiz {
        // Get a snippet from Gemini
        val fullSnippet = geminiService.generateSnippet(level)
        
        // Split the snippet into words
        val words = fullSnippet.split(" ")
        
        // Choose a random word to remove (excluding first and last words)
        val targetIndex = (1 until words.size - 1).random()
        val correctAnswer = words[targetIndex]
        
        // Create the snippet with blank
        val snippetWithBlank = words.mapIndexed { index, word ->
            if (index == targetIndex) "____" else word
        }.joinToString(" ")
        
        // Get 3 random words for wrong options
        val wrongOptions = wordRepository.getRandomWords(3)
        
        // Create options list with correct answer and wrong options
        val options = (wrongOptions + correctAnswer).shuffled()
        
        return ListeningQuiz(
            fullSnippet = fullSnippet,
            snippetWithBlank = snippetWithBlank,
            correctAnswer = correctAnswer,
            options = options
        )
    }
} 