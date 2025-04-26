package com.example.lungoapp.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val englishLevel: String
) 