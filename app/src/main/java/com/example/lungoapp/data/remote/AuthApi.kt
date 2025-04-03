package com.example.lungoapp.data.remote

interface AuthApi {
    suspend fun login(request: LoginRequest): UserDto {
        // Mock successful login
        return UserDto(
            id = "1",
            email = request.email,
            name = "Test User",
            englishLevel = "Intermediate"
        )
    }

    suspend fun register(request: RegisterRequest): UserDto {
        // Mock successful registration
        return UserDto(
            id = "1",
            email = request.email,
            name = request.name,
            englishLevel = request.englishLevel
        )
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val englishLevel: String
)

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val englishLevel: String
) 