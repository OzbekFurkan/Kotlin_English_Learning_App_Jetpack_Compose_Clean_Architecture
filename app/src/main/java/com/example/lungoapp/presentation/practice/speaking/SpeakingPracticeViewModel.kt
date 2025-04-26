package com.example.lungoapp.presentation.practice.speaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.services.GeminiService
import com.example.lungoapp.services.SpeechToTextService
import com.example.lungoapp.services.TextToSpeechService
import com.example.lungoapp.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Message(
    val text: String,
    val isUser: Boolean
)

@HiltViewModel
class SpeakingPracticeViewModel @Inject constructor(
    private val speechToTextService: SpeechToTextService,
    private val textToSpeechService: TextToSpeechService,
    private val geminiService: GeminiService,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    init {
        viewModelScope.launch {
            speechToTextService.recognitionFlow.collect { text ->
                if (text.isNotEmpty()) {
                    addUserMessage(text)
                }
            }
        }
    }

    fun startListening() {
        _isListening.value = true
        speechToTextService.startListening()
    }

    fun stopListening() {
        _isListening.value = false
        speechToTextService.stopListening()
    }

    private fun addUserMessage(text: String) {
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(Message(text, true))
        _messages.value = currentMessages
        generateResponse(text)
    }

    private fun generateResponse(userText: String) {
        viewModelScope.launch {
            try {
                val prompt = "As an English language teacher, provide a natural and conversational response to this: \"$userText\". Keep it brief and friendly."
                val response = geminiService.generateText(prompt)
                
                val currentMessages = _messages.value.toMutableList()
                currentMessages.add(Message(response, false))
                _messages.value = currentMessages
                
                textToSpeechService.speak(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    suspend fun saveBookmark(word: String) {
        try {
            bookmarkRepository.saveBookmark(word)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 