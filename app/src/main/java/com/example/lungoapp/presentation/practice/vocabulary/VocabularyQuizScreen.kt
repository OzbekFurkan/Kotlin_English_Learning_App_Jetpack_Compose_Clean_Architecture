package com.example.lungoapp.presentation.practice.vocabulary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lungoapp.ui.components.ClickableWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyQuizScreen(
    navController: NavController,
    viewModel: VocabularyQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNextQuestion()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState) {
                is QuizState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is QuizState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading question",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is QuizState.Success -> {
                    currentQuestion?.let { question ->
                        Text(
                            text = "What is the Turkish meaning of:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        ClickableWord(
                            word = question.word,
                            onSave = { viewModel.saveBookmark(question.word) },
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        question.options.forEach { option ->
                            OutlinedButton(
                                onClick = { viewModel.checkAnswer(option) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ClickableWord(
                                    word = option,
                                    onSave = { viewModel.saveBookmark(option) }
                                )
                            }
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No question available",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
} 