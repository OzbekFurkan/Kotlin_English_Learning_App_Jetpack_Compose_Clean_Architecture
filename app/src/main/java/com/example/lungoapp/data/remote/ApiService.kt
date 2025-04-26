package com.example.lungoapp.data.remote

import com.example.lungoapp.data.model.Bookmark
import com.example.lungoapp.data.model.LoginRequest
import com.example.lungoapp.data.model.LoginResponse
import com.example.lungoapp.data.model.UserResponse
import retrofit2.http.*

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
} 