package com.example.lungoapp.data.repository

import com.example.lungoapp.data.remote.AuthApi
import com.example.lungoapp.data.remote.LoginRequest
import com.example.lungoapp.data.remote.RegisterRequest
import com.example.lungoapp.data.remote.UserDto
import com.example.lungoapp.domain.model.User
import com.example.lungoapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            Result.success(response.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        englishLevel: String
    ): Result<User> {
        return try {
            val response = api.register(RegisterRequest(email, password, name, englishLevel))
            Result.success(response.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        // Clear local storage, tokens, etc.
    }

    override fun isUserLoggedIn(): Boolean {
        // Check if user is logged in (e.g., check token in local storage)
        return false
    }

    private fun UserDto.toUser(): User {
        return User(
            id = id,
            email = email,
            name = name,
            englishLevel = englishLevel
        )
    }
} 