package com.example.lungoapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.domain.model.User
import com.example.lungoapp.domain.repository.AuthRepository
import com.example.lungoapp.presentation.onboarding.PersonalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "An error occurred")
                }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        englishLevel: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(email, password, name, englishLevel)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "An error occurred")
                }
        }
    }

    fun registerWithPersonalInfo(
        email: String,
        password: String,
        name: String,
        englishLevel: String,
        personalInfo: PersonalInfo
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.registerWithPersonalInfo(email, password, name, englishLevel, personalInfo)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "An error occurred")
                }
        }
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
} 