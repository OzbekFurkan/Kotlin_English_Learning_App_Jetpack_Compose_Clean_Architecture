package com.example.lungoapp.data.model

data class ReadingPractice(
    val passage: String,
    val level: String,
    val wordCount: Int,
    val readingTime: Int // in seconds
) 