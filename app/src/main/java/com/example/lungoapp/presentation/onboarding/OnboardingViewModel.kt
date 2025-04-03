package com.example.lungoapp.presentation.onboarding

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
import javax.inject.Inject


@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val _personalInfo = MutableStateFlow(PersonalInfo())
    val personalInfo: StateFlow<PersonalInfo> = _personalInfo.asStateFlow()

    private val _testScore = MutableStateFlow(0)
    val testScore: StateFlow<Int> = _testScore.asStateFlow()

    fun updatePersonalInfo(name: String, age: String, occupation: String) {
        _personalInfo.value = PersonalInfo(name, age, occupation)
    }

    fun updateTestScore(score: Int) {
        _testScore.value = score
    }

    fun getEnglishLevel(): String {
        return when {
            _testScore.value <= 3 -> "Beginner"
            _testScore.value <= 6 -> "Intermediate"
            _testScore.value <= 8 -> "Upper Intermediate"
            else -> "Advanced"
        }
    }
}

data class PersonalInfo(
    val name: String = "",
    val age: String = "",
    val occupation: String = ""
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