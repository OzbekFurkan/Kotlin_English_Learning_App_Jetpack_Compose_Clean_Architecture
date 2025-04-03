package com.example.lungoapp.domain.use_case.auth

import com.example.lungoapp.domain.model.User
import com.example.lungoapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
} 