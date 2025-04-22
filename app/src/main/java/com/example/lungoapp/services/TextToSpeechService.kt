package com.example.lungoapp.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TextToSpeechService"

@Singleton
class TextToSpeechService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val initDeferred = CompletableDeferred<Boolean>()

    init {
        initialize()
    }

    private fun initialize() {
        Log.d(TAG, "Initializing TextToSpeech...")
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported or missing data")
                    isInitialized = false
                    initDeferred.complete(false)
                } else {
                    Log.d(TAG, "TextToSpeech initialized successfully")
                    isInitialized = true
                    setupUtteranceListener()
                    initDeferred.complete(true)
                }
            } else {
                Log.e(TAG, "TextToSpeech initialization failed with status: $status")
                isInitialized = false
                initDeferred.complete(false)
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "Started speaking utterance: $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "Finished speaking utterance: $utteranceId")
            }

            override fun onError(utteranceId: String?) {
                Log.e(TAG, "Error speaking utterance: $utteranceId")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(TAG, "Error speaking utterance: $utteranceId, error code: $errorCode")
            }
        })
    }

    suspend fun speak(text: String): Boolean = withContext(Dispatchers.Main) {
        if (!isInitialized) {
            Log.w(TAG, "TextToSpeech not initialized, waiting for initialization...")
            isInitialized = initDeferred.await()
            if (!isInitialized) {
                Log.e(TAG, "TextToSpeech initialization failed")
                return@withContext false
            }
        }

        try {
            stop()
            Log.d(TAG, "Speaking text: $text")
            val params = HashMap<String, String>().apply {
                put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId")
            }
            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
            if (result == TextToSpeech.SUCCESS) {
                Log.d(TAG, "Speech queued successfully")
                true
            } else {
                Log.e(TAG, "Failed to queue speech, result: $result")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during speech synthesis", e)
            false
        }
    }

    fun stop() {
        try {
            tts?.stop()
            Log.d(TAG, "Speech stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech", e)
        }
    }

    fun shutdown() {
        try {
            tts?.shutdown()
            isInitialized = false
            Log.d(TAG, "TextToSpeech service shut down")
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TextToSpeech", e)
        }
    }
} 