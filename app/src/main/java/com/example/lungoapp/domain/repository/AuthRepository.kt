package com.example.lungoapp.domain.repository

import com.example.lungoapp.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String, englishLevel: String): Result<User>
    suspend fun logout()
    fun isUserLoggedIn(): Boolean
} 