package com.example.lungoapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WordApi {
    @GET("words/random")
    suspend fun getRandomWord(): Response<WordDto>

    @GET("words/random/{count}")
    suspend fun getRandomWords(@Path("count") count: Int): Response<List<WordDto>>
}

data class WordDto(
    val word: String,
    val eng_level: String,
    val count: Int
) 