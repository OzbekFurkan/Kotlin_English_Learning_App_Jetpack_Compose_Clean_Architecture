package com.example.lungoapp.domain.use_case.auth

import com.example.lungoapp.domain.model.User
import com.example.lungoapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        englishLevel: String
    ): Result<User> {
        return repository.register(email, password, name, englishLevel)
    }
} 