package com.example.lungoapp.data.repository

import com.example.lungoapp.data.model.ReadingPractice
import com.example.lungoapp.services.GeminiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingPracticeRepository @Inject constructor(
    private val geminiService: GeminiService
) {
    suspend fun generateReadingPractice(level: String): ReadingPractice {
        val prompt = """
            Generate a short reading passage suitable for $level English learners.
            The passage should be:
            - 100-150 words long
            - Use simple vocabulary and grammar
            - Be engaging and interesting
            - Include a variety of sentence structures
            - Be appropriate for reading practice
        """.trimIndent()

        val passage = geminiService.generateText(prompt)
        val wordCount = passage.split("\\s+".toRegex()).size
        val readingTime = (wordCount / 2).coerceAtLeast(30) // Assuming average reading speed of 2 words per second

        return ReadingPractice(
            passage = passage,
            level = level,
            wordCount = wordCount,
            readingTime = readingTime
        )
    }
} 