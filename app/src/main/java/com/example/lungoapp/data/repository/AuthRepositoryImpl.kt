package com.example.lungoapp.data.repository

import android.util.Log
import com.example.lungoapp.data.manager.UserManager
import com.example.lungoapp.data.remote.AuthApi
import com.example.lungoapp.data.remote.LoginRequest
import com.example.lungoapp.data.remote.RegisterRequest
import com.example.lungoapp.data.remote.UserDto
import com.example.lungoapp.domain.model.User
import com.example.lungoapp.domain.repository.AuthRepository
import com.example.lungoapp.presentation.onboarding.PersonalInfo
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val userManager: UserManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d("AuthRepositoryImpl", "Attempting login for email: $email")
            val response = api.login(LoginRequest(email, password))
            Log.d("AuthRepositoryImpl", "Login successful, token received: ${response.access_token}")
            
            // Save token first
            userManager.saveUserData(0, "", email, response.access_token)
            Log.d("AuthRepositoryImpl", "Token saved")
            
            // Now get user data with the token
            val userResponse = api.getUserData()
            Log.d("AuthRepositoryImpl", "User data retrieved: ${userResponse.user_id}")
            
            val user = User(
                id = userResponse.user_id.toString(),
                email = email,
                name = userResponse.username,
                englishLevel = when (userResponse.level_id) {
                    1 -> "Beginner"
                    2 -> "Intermediate"
                    3 -> "Advanced"
                    else -> "Unknown"
                }
            )
            // Update user data with correct ID and name
            userManager.saveUserData(user.id.toInt(), user.name, user.email, response.access_token)
            Log.d("AuthRepositoryImpl", "User data updated")
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Login failed", e)
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
            Log.d("AuthRepositoryImpl", "Attempting registration for email: $email")
            val response = api.register(
                RegisterRequest(
                    username = name,
                    email = email,
                    password = password,
                    gender = "male", // Default value
                    age = 25, // Default value
                    edu_status = "student", // Default value
                    prev_edu_year = 2000, // Default value
                    level_id = when (englishLevel.uppercase()) {
                        "A1" -> 0
                        "A2" -> 1
                        "B1" -> 2
                        "B2" -> 3
                        "C1" -> 4
                        "C2" -> 5
                        else -> 3 // Default to B2 if level is unknown
                    }
                )
            )
            Log.d("AuthRepositoryImpl", "Registration successful")
            
            val user = response.toUser()
            // After registration, we need to login to get the token
            val loginResponse = api.login(LoginRequest(email, password))
            Log.d("AuthRepositoryImpl", "Auto-login successful, token received: ${loginResponse.access_token}")
            
            // Save token first
            userManager.saveUserData(0, "", email, loginResponse.access_token)
            Log.d("AuthRepositoryImpl", "Token saved")
            
            // Now get user data with the token
            val userResponse = api.getUserData()
            Log.d("AuthRepositoryImpl", "User data retrieved: ${userResponse.user_id}")
            
            // Update user data with correct ID and name
            userManager.saveUserData(user.id.toInt(), user.name, user.email, loginResponse.access_token)
            Log.d("AuthRepositoryImpl", "User data updated")
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Registration failed", e)
            Result.failure(e)
        }
    }

    override suspend fun registerWithPersonalInfo(
        email: String,
        password: String,
        name: String,
        englishLevel: String,
        personalInfo: PersonalInfo
    ): Result<User> {
        return try {
            Log.d("AuthRepositoryImpl", "Attempting registration with personal info for email: $email")
            val response = api.register(
                RegisterRequest(
                    username = name,
                    email = email,
                    password = password,
                    gender = when (personalInfo.gender) {
                        0 -> "male"
                        1 -> "female"
                        else -> "male"
                    },
                    age = try {
                        personalInfo.age.toInt()
                    } catch (e: NumberFormatException) {
                        Log.e("AuthRepositoryImpl", "Failed to convert age to integer: ${personalInfo.age}")
                        25 // Default age if conversion fails
                    },
                    edu_status = when (personalInfo.educationStatus) {
                        0 -> "first_school"
                        1 -> "middle_school"
                        2 -> "high_school"
                        3 -> "associate"
                        4 -> "bachelor"
                        5 -> "master"
                        6 -> "phd"
                        else -> "null"
                    },
                    prev_edu_year = personalInfo.educationYears,
                    level_id = when (englishLevel.uppercase()) {
                        "A1" -> 0
                        "A2" -> 1
                        "B1" -> 2
                        "B2" -> 3
                        "C1" -> 4
                        "C2" -> 5
                        else -> 3 // Default to B2 if level is unknown
                    }
                )
            )
            Log.d("AuthRepositoryImpl", "Registration with personal info successful")
            
            val user = response.toUser()
            // After registration, we need to login to get the token
            val loginResponse = api.login(LoginRequest(email, password))
            Log.d("AuthRepositoryImpl", "Auto-login successful, token received: ${loginResponse.access_token}")
            
            // Save token first
            userManager.saveUserData(0, "", email, loginResponse.access_token)
            Log.d("AuthRepositoryImpl", "Token saved")
            
            // Now get user data with the token
            val userResponse = api.getUserData()
            Log.d("AuthRepositoryImpl", "User data retrieved: ${userResponse.user_id}")
            
            // Update user data with correct ID and name
            userManager.saveUserData(user.id.toInt(), user.name, user.email, loginResponse.access_token)
            Log.d("AuthRepositoryImpl", "User data updated")
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Registration with personal info failed", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        Log.d("AuthRepositoryImpl", "Logging out user")
        // Clear user data from UserManager
        userManager.clearUserData()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        // Check if user is logged in by checking if we have a user ID
        return userManager.userId.firstOrNull() != null
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