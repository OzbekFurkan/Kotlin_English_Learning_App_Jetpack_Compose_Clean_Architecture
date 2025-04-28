package com.example.lungoapp.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.lungoapp.data.remote.ApiService
import com.example.lungoapp.data.remote.CefrPredictionRequest

private const val TAG = "OnboardingViewModel"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _personalInfo = MutableStateFlow(PersonalInfo())
    val personalInfo: StateFlow<PersonalInfo> = _personalInfo.asStateFlow()

    private val _cefrLevel = MutableStateFlow<String?>(null)
    val cefrLevel: StateFlow<String?> = _cefrLevel.asStateFlow()

    private val _questionAnswers = MutableStateFlow<List<Int>>(List(10) { 0 })
    val questionAnswers: StateFlow<List<Int>> = _questionAnswers.asStateFlow()

    init {
        Log.d(TAG, "OnboardingViewModel initialized with hash: ${hashCode()}")
        Log.d(TAG, "Initial CEFR level: ${_cefrLevel.value}")
        Log.d(TAG, "Initial personal info: ${_personalInfo.value}")
    }

    private fun mapCefrLevel(level: Int): String {
        return when (level) {
            0 -> "A1"
            1 -> "A2"
            2 -> "B1"
            3 -> "B2"
            4 -> "C1"
            else -> "Unknown"
        }
    }

    fun updatePersonalInfo(
        name: String,
        age: String,
        occupation: String,
        gender: Int,
        educationStatus: Int,
        educationYears: Int
    ) {
        Log.d(TAG, "Updating personal info with values:")
        Log.d(TAG, "Name: $name")
        Log.d(TAG, "Age: $age")
        Log.d(TAG, "Occupation: $occupation")
        Log.d(TAG, "Gender: $gender")
        Log.d(TAG, "Education Status: $educationStatus")
        Log.d(TAG, "Education Years: $educationYears")

        val newPersonalInfo = PersonalInfo(
            name = name,
            age = age,
            occupation = occupation,
            gender = gender,
            educationStatus = educationStatus,
            educationYears = educationYears
        )
        
        _personalInfo.value = newPersonalInfo
        Log.d(TAG, "Updated personal info in ViewModel: ${_personalInfo.value}")
    }

    fun updateQuestionAnswer(questionIndex: Int, answer: Int) {
        val currentAnswers = _questionAnswers.value.toMutableList()
        currentAnswers[questionIndex] = answer
        _questionAnswers.value = currentAnswers
        Log.d(TAG, "Updated answer for question ${questionIndex + 1}: $answer")
        Log.d(TAG, "Current answers: ${_questionAnswers.value}")
    }

    fun predictCefrLevel() {
        val answers = _questionAnswers.value
        val personalInfo = _personalInfo.value
        
        if (answers.size != 10) {
            Log.e(TAG, "Invalid number of answers: ${answers.size}")
            return
        }

        Log.d(TAG, "Current personal info before prediction: $personalInfo")
        
        if (personalInfo.age.isEmpty()) {
            Log.e(TAG, "Age is empty in personal info")
            return
        }

        Log.d(TAG, "predictCefrLevel called with answers: $answers")
        Log.d(TAG, "Personal info: $personalInfo")
        Log.d(TAG, "ViewModel hash: ${hashCode()}")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting CEFR level prediction with answers: $answers")
                Log.d(TAG, "Current CEFR level before prediction: ${_cefrLevel.value}")

                // Convert age string to integer, defaulting to 25 if conversion fails
                val ageInt = try {
                    personalInfo.age.toInt()
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "Failed to convert age to integer: ${personalInfo.age}")
                    25
                }

                val request = CefrPredictionRequest(
                    gender = personalInfo.gender,
                    age = ageInt,
                    edu_status = personalInfo.educationStatus,
                    prev_edu_ye = personalInfo.educationYears,
                    q1 = answers[0],
                    q2 = answers[1],
                    q3 = answers[2],
                    q4 = answers[3],
                    q5 = answers[4],
                    q6 = answers[5],
                    q7 = answers[6],
                    q8 = answers[7],
                    q9 = answers[8],
                    q10 = answers[9]
                )
                
                Log.d(TAG, "Sending request to API: $request")
                val response = apiService.predictCefrLevel(request)
                Log.d(TAG, "Received CEFR level prediction: ${response.predicted_cefr_level}")
                
                val mappedLevel = mapCefrLevel(response.predicted_cefr_level)
                Log.d(TAG, "Mapped CEFR level: $mappedLevel")
                
                _cefrLevel.value = mappedLevel
                Log.d(TAG, "Updated CEFR level in ViewModel: ${_cefrLevel.value}")
            } catch (e: Exception) {
                Log.e(TAG, "Error predicting CEFR level", e)
                _cefrLevel.value = null
            }
        }
    }
}

data class PersonalInfo(
    val name: String = "",
    val age: String = "",
    val occupation: String = "",
    val gender: Int = 1, // 1: Male, 2: Female
    val educationStatus: Int = 2, // 1: High School, 2: University, 3: Graduate
    val educationYears: Int = 12 // Default to 12 years
)

class PersonalInfoPagingSource : PagingSource<Int, PersonalInfo>() {
    override fun getRefreshKey(state: PagingState<Int, PersonalInfo>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PersonalInfo> {
        return LoadResult.Page(
            data = listOf(PersonalInfo()),
            prevKey = null,
            nextKey = null
        )
    }
}

class TestScorePagingSource : PagingSource<Int, Int>() {
    override fun getRefreshKey(state: PagingState<Int, Int>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
        return LoadResult.Page(
            data = listOf(0),
            prevKey = null,
            nextKey = null
        )
    }
} 