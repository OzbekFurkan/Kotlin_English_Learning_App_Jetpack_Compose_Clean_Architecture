package com.example.lungoapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("token")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): UserDto

    @GET("user")
    suspend fun getUserData(): UserDto
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val gender: String,
    val age: Int,
    val edu_status: String,
    val prev_edu_year: Int,
    val level_id: Int
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)

data class UserDto(
    val user_id: Int,
    val username: String,
    val email: String,
    val gender: String,
    val age: Int,
    val edu_status: String,
    val prev_edu_year: Int,
    val level_id: Int,
    val user_reg_date: String
) 