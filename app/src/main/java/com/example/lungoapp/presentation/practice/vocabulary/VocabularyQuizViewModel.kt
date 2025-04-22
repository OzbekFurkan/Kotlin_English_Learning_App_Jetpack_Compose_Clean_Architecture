package com.example.lungoapp.presentation.practice.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.data.repository.WordRepository
import com.example.lungoapp.services.TranslationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val word: String,
    val correctAnswer: String,
    val options: List<String>
)

sealed class QuizState {
    object Loading : QuizState()
    object Error : QuizState()
    object Success : QuizState()
}

@HiltViewModel
class VocabularyQuizViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val translationService: TranslationService
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizState>(QuizState.Loading)
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private val _currentQuestion = MutableStateFlow<QuizQuestion?>(null)
    val currentQuestion: StateFlow<QuizQuestion?> = _currentQuestion.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    fun loadNextQuestion() {
        viewModelScope.launch {
            try {
                _uiState.value = QuizState.Loading
                println("Loading next question...")
                
                // Get a random word from the database
                val word = wordRepository.getRandomWord()
                println("Got random word: $word")
                
                // Get the translation
                val translationResponse = translationService.translateWord(word)
                if (!translationResponse.isSuccessful) {
                    println("Translation failed: ${translationResponse.code()}")
                    throw Exception("Translation failed")
                }
                val translation = translationResponse.body()?.translated_text
                if (translation == null) {
                    println("Translation response body is null")
                    throw Exception("Translation response body is null")
                }
                println("Got translation: $translation")
                
                // Get 3 random words for wrong options
                val wrongWords = wordRepository.getRandomWords(3)
                println("Got wrong words: $wrongWords")
                
                val wrongTranslations = wrongWords.map { wrongWord ->
                    val wrongTranslationResponse = translationService.translateWord(wrongWord)
                    if (!wrongTranslationResponse.isSuccessful) {
                        println("Wrong word translation failed: ${wrongTranslationResponse.code()}")
                        throw Exception("Translation failed")
                    }
                    val wrongTranslation = wrongTranslationResponse.body()?.translated_text
                    if (wrongTranslation == null) {
                        println("Wrong word translation response body is null")
                        throw Exception("Translation response body is null")
                    }
                    wrongTranslation
                }
                println("Got wrong translations: $wrongTranslations")
                
                // Create options list with correct answer and wrong options
                val options = (wrongTranslations + translation).shuffled()
                println("Created options: $options")
                
                _currentQuestion.value = QuizQuestion(
                    word = word,
                    correctAnswer = translation,
                    options = options
                )
                
                _uiState.value = QuizState.Success
                println("Question loaded successfully")
            } catch (e: Exception) {
                println("Error loading question: ${e.message}")
                e.printStackTrace()
                _uiState.value = QuizState.Error
            }
        }
    }

    fun checkAnswer(selectedAnswer: String) {
        viewModelScope.launch {
            val currentQuestion = _currentQuestion.value ?: return@launch
            
            if (selectedAnswer == currentQuestion.correctAnswer) {
                _score.value += 1
                println("Correct answer! Score: ${_score.value}")
            } else {
                println("Wrong answer. Correct was: ${currentQuestion.correctAnswer}")
            }
            
            // Load next question after a delay
            delay(1000) // Add a small delay to show feedback
            loadNextQuestion()
        }
    }
} 