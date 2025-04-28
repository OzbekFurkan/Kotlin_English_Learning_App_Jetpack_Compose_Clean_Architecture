package com.example.lungoapp.domain.repository

import com.example.lungoapp.domain.model.User
import com.example.lungoapp.presentation.onboarding.PersonalInfo

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String, englishLevel: String): Result<User>
    suspend fun registerWithPersonalInfo(
        email: String,
        password: String,
        name: String,
        englishLevel: String,
        personalInfo: PersonalInfo
    ): Result<User>
    suspend fun logout()
    suspend fun isUserLoggedIn(): Boolean
} 