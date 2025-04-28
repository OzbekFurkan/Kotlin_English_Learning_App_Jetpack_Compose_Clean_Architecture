package com.example.lungoapp.data.remote

import com.example.lungoapp.data.model.Bookmark
import com.example.lungoapp.data.model.LoginRequest
import com.example.lungoapp.data.model.LoginResponse
import com.example.lungoapp.data.model.UserResponse
import retrofit2.http.*

data class CefrPredictionRequest(
    val gender: Int,
    val age: Int,
    val edu_status: Int,
    val prev_edu_ye: Int,
    val q1: Int,
    val q2: Int,
    val q3: Int,
    val q4: Int,
    val q5: Int,
    val q6: Int,
    val q7: Int,
    val q8: Int,
    val q9: Int,
    val q10: Int
)

data class CefrPredictionResponse(
    val predicted_cefr_level: Int
)

interface ApiService {
    @POST("bookmarks/")
    suspend fun createBookmark(@Body request: Map<String, String>): Bookmark

    @GET("bookmarks/")
    suspend fun getBookmarks(): List<Bookmark>

    @DELETE("bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(@Path("bookmarkId") bookmarkId: Int)

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): UserResponse

    @POST("user/bookmark")
    suspend fun saveBookmark(
        @Header("Authorization") token: String,
        @Body bookmark: Bookmark
    ): Bookmark

    @GET("user/bookmarks")
    suspend fun getBookmarks(
        @Header("Authorization") token: String
    ): List<Bookmark>

    @POST("predict_cefr_level")
    suspend fun predictCefrLevel(@Body request: CefrPredictionRequest): CefrPredictionResponse
} 