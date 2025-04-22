package com.example.lungoapp.data.model

data class ListeningQuiz(
    val fullSnippet: String,
    val snippetWithBlank: String,
    val correctAnswer: String,
    val options: List<String>
) 