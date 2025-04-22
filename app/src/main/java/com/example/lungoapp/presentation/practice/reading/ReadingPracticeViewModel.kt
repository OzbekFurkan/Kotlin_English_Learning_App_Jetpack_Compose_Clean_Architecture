package com.example.lungoapp.presentation.practice.reading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.data.model.ReadingPractice
import com.example.lungoapp.data.repository.ReadingPracticeRepository
import com.example.lungoapp.services.SpeechToTextService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ReadingPracticeViewModel"

@HiltViewModel
class ReadingPracticeViewModel @Inject constructor(
    private val repository: ReadingPracticeRepository,
    private val speechToTextService: SpeechToTextService
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _userSpeech = MutableStateFlow("")
    val userSpeech: StateFlow<String> = _userSpeech.asStateFlow()

    init {
        loadPassage()
        setupSpeechRecognition()
    }

    private fun setupSpeechRecognition() {
        speechToTextService.recognitionFlow
            .onEach { text ->
                _uiState.update { currentState ->
                    if (currentState is UiState.Success) {
                        currentState.copy(spokenText = text)
                    } else {
                        currentState
                    }
                }
                Log.d(TAG, "Received speech: $text")
            }
            .launchIn(viewModelScope)
    }

    private fun loadPassage() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val practice = repository.generateReadingPractice("intermediate")
                _uiState.value = UiState.Success(
                    passage = practice.passage,
                    spokenText = "",
                    isListening = false
                )
                Log.d(TAG, "Successfully loaded reading practice")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading reading practice", e)
                _uiState.value = UiState.Error("Failed to load reading practice: ${e.message}")
            }
        }
    }

    fun startListening() {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                currentState.copy(isListening = true)
            } else {
                currentState
            }
        }
        viewModelScope.launch {
            speechToTextService.startListening()
        }
    }

    fun stopListening() {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                currentState.copy(isListening = false)
            } else {
                currentState
            }
        }
        speechToTextService.stopListening()
    }

    fun calculateAccuracy(): Float {
        val state = _uiState.value
        if (state is UiState.Success) {
            val passageWords = state.passage.lowercase().split("\\s+".toRegex())
            val spokenWords = state.spokenText.lowercase().split("\\s+".toRegex())
            
            if (passageWords.isEmpty() || spokenWords.isEmpty()) {
                return 0f
            }

            var totalScore = 0f
            var matchedWords = 0

            // Create a map of word to its positions in the passage
            val wordPositions = mutableMapOf<String, MutableList<Int>>()
            passageWords.forEachIndexed { index, word ->
                wordPositions.getOrPut(word) { mutableListOf() }.add(index)
            }

            // Score each spoken word
            spokenWords.forEachIndexed { spokenIndex, spokenWord ->
                if (wordPositions.containsKey(spokenWord)) {
                    val positions = wordPositions[spokenWord]!!
                    // Find the closest position to the spoken word's position
                    val closestPosition = positions.minByOrNull { 
                        kotlin.math.abs(it - spokenIndex) 
                    } ?: positions.first()
                    
                    // Calculate position score (1.0 for exact match, decreasing with distance)
                    val positionScore = 1.0f - (kotlin.math.abs(closestPosition - spokenIndex).toFloat() / passageWords.size)
                    
                    // Add to total score
                    totalScore += positionScore.coerceIn(0f, 1.0f)
                    matchedWords++
                }
            }

            // Calculate final score based only on the spoken words
            val wordExistenceScore = matchedWords.toFloat() / spokenWords.size
            val positionScore = if (matchedWords > 0) totalScore / matchedWords else 0f
            
            // Combine scores with weights (70% for word existence, 30% for position)
            val finalScore = (wordExistenceScore * 0.7f + positionScore * 0.3f) * 100
            
            Log.d(TAG, "Accuracy calculation: " +
                "Word existence: $wordExistenceScore, " +
                "Position: $positionScore, " +
                "Final: $finalScore"
            )
            
            return finalScore.coerceIn(0f, 100f)
        }
        return 0f
    }

    sealed class UiState {
        object Loading : UiState()
        data class Error(val message: String) : UiState()
        data class Success(
            val passage: String,
            val spokenText: String,
            val isListening: Boolean
        ) : UiState()
    }

    override fun onCleared() {
        super.onCleared()
        speechToTextService.stopListening()
    }
} 