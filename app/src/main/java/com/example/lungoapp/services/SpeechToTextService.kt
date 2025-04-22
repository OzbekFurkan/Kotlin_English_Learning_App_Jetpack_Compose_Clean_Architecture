package com.example.lungoapp.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SpeechToTextService"

@Singleton
class SpeechToTextService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val recognitionChannel = Channel<String>()
    val recognitionFlow = recognitionChannel.receiveAsFlow()
    private var isListening = false

    fun startListening() {
        try {
            if (isListening) {
                Log.d(TAG, "Already listening, restarting...")
                stopListening()
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d(TAG, "Ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "Beginning of speech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Optional: Handle volume changes
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                        // Optional: Handle audio buffer
                    }

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "End of speech")
                        // Restart listening when speech ends
                        if (isListening) {
                            startListening()
                        }
                    }

                    override fun onError(error: Int) {
                        Log.e(TAG, "Speech recognition error: $error")
                        when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> {
                                // No speech detected, restart listening
                                if (isListening) {
                                    startListening()
                                }
                            }
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                                // Speech timeout, restart listening
                                if (isListening) {
                                    startListening()
                                }
                            }
                            else -> {
                                recognitionChannel.trySend("")
                            }
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.get(0) ?: ""
                        Log.d(TAG, "Recognition results: $text")
                        recognitionChannel.trySend(text)
                        
                        // Restart listening after getting results
                        if (isListening) {
                            startListening()
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.get(0) ?: ""
                        if (text.isNotEmpty()) {
                            Log.d(TAG, "Partial results: $text")
                            recognitionChannel.trySend(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // Optional: Handle events
                    }
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Disable timeout
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, Long.MAX_VALUE)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, Long.MAX_VALUE)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, Long.MAX_VALUE)
            }

            isListening = true
            speechRecognizer?.startListening(intent)
            Log.d(TAG, "Started listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            recognitionChannel.trySend("")
        }
    }

    fun stopListening() {
        try {
            isListening = false
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognition", e)
        }
    }
} 