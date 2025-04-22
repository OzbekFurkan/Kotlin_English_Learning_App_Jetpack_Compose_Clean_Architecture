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
            // Store the token in a secure storage (SharedPreferences or DataStore)
            Result.success(
                User(
                    id = "1", // This should be updated with actual user ID from backend
                    email = email,
                    name = "User", // This should be updated with actual username from backend
                    englishLevel = "Intermediate" // This should be updated with actual level from backend
                )
            )
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
            val response = api.register(
                RegisterRequest(
                    username = name,
                    email = email,
                    password = password,
                    gender = "male", // Default values for now
                    age = 20,
                    edu_status = "student",
                    prev_edu_year = 2000,
                    level_id = when (englishLevel.lowercase()) {
                        "beginner" -> 1
                        "intermediate" -> 2
                        "advanced" -> 3
                        else -> 2
                    }
                )
            )
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
            id = user_id.toString(),
            email = email,
            name = username,
            englishLevel = when (level_id) {
                1 -> "Beginner"
                2 -> "Intermediate"
                3 -> "Advanced"
                else -> "Unknown"
            }
        )
    }
} 