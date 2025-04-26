package com.example.lungoapp.presentation.practice.listening

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.data.manager.UserManager
import com.example.lungoapp.data.model.ListeningQuiz
import com.example.lungoapp.data.repository.ListeningQuizRepository
import com.example.lungoapp.data.repository.BookmarkRepository
import com.example.lungoapp.services.TextToSpeechService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ListeningQuizState {
    object Loading : ListeningQuizState()
    data class Error(val message: String) : ListeningQuizState()
    data class Success(val quiz: ListeningQuiz) : ListeningQuizState()
}

private const val TAG = "ListeningQuizViewModel"

@HiltViewModel
class ListeningQuizViewModel @Inject constructor(
    private val repository: ListeningQuizRepository,
    private val textToSpeechService: TextToSpeechService,
    private val bookmarkRepository: BookmarkRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListeningQuizState>(ListeningQuizState.Loading)
    val uiState: StateFlow<ListeningQuizState> = _uiState.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    init {
        loadNextQuestion()
    }

    fun loadNextQuestion() {
        viewModelScope.launch {
            try {
                _uiState.value = ListeningQuizState.Loading
                val quiz = repository.generateQuiz("intermediate")
                _uiState.value = ListeningQuizState.Success(quiz)
                Log.d(TAG, "Successfully loaded new question: ${quiz.fullSnippet}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading next question", e)
                _uiState.value = ListeningQuizState.Error("Failed to load question: ${e.message}")
            }
        }
    }

    fun playSnippet() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is ListeningQuizState.Success) {
                    _isPlaying.value = true
                    Log.d(TAG, "Playing snippet: ${currentState.quiz.fullSnippet}")
                    val success = textToSpeechService.speak(currentState.quiz.fullSnippet)
                    if (!success) {
                        Log.e(TAG, "Failed to play snippet")
                        _uiState.value = ListeningQuizState.Error("Failed to play audio")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error playing snippet", e)
                _uiState.value = ListeningQuizState.Error("Failed to play audio: ${e.message}")
            } finally {
                _isPlaying.value = false
            }
        }
    }

    fun stopPlaying() {
        textToSpeechService.stop()
        _isPlaying.value = false
    }

    fun checkAnswer(selectedAnswer: String) {
        val currentState = _uiState.value
        if (currentState is ListeningQuizState.Success) {
            if (selectedAnswer == currentState.quiz.correctAnswer) {
                _score.value += 1
                Log.d(TAG, "Correct answer! New score: ${_score.value}")
            } else {
                Log.d(TAG, "Incorrect answer. Expected: ${currentState.quiz.correctAnswer}, Got: $selectedAnswer")
            }
            loadNextQuestion()
        }
    }

    fun saveBookmark(word: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.saveBookmark(word)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving bookmark", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeechService.shutdown()
    }
} 