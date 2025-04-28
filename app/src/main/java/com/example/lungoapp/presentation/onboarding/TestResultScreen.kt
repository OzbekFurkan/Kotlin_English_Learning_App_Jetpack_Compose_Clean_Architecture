package com.example.lungoapp.presentation.onboarding

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private const val TAG = "TestResultScreen"

@Composable
fun TestResultScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    Log.d(TAG, "TestResultScreen recomposing with ViewModel hash: ${viewModel.hashCode()}")
    
    val cefrLevel by viewModel.cefrLevel.collectAsState()
    val personalInfo by viewModel.personalInfo.collectAsState()
    
    // Log the CEFR level and personal info when they change
    LaunchedEffect(cefrLevel) {
        Log.d(TAG, "CEFR Level updated in LaunchedEffect: $cefrLevel")
    }
    
    LaunchedEffect(personalInfo) {
        Log.d(TAG, "Personal info in TestResultScreen: $personalInfo")
    }

    // Trigger prediction when screen is first composed
    LaunchedEffect(Unit) {
        Log.d(TAG, "Triggering CEFR level prediction")
        viewModel.predictCefrLevel()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Test Result",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (cefrLevel) {
            null -> {
                Log.d(TAG, "Showing loading state")
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Calculating your CEFR level...",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            else -> {
                Log.d(TAG, "Showing CEFR level: $cefrLevel")
                Text(
                    text = "Your CEFR Level: $cefrLevel",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = cefrLevel != null
        ) {
            Text("Next")
        }
    }
} 