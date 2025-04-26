package com.example.lungoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lungoapp.data.model.Bookmark
import com.example.lungoapp.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _bookmarks.value = bookmarkRepository.getBookmarks()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load bookmarks"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveBookmark(word: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                bookmarkRepository.saveBookmark(word)
                loadBookmarks() // Refresh the list after saving
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to save bookmark"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 