package com.example.lungoapp.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EnglishTestScreen(
    onFinishClick: (Int) -> Unit
) {
    var currentQuestion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    
    val questions = listOf(
        "How long have you been learning English?" to listOf(
            "Never",
            "Less than 1 year",
            "1-3 years",
            "More than 3 years"
        ),
        "Can you understand English movies without subtitles?" to listOf(
            "Not at all",
            "Sometimes",
            "Most of the time",
            "Always"
        ),
        "Do you feel comfortable speaking English?" to listOf(
            "Not at all",
            "A little",
            "Mostly",
            "Very comfortable"
        ),
        "How well can you write in English?" to listOf(
            "Basic sentences only",
            "Simple paragraphs",
            "Complex paragraphs",
            "Professional writing"
        ),
        "Can you read English books without difficulty?" to listOf(
            "Not at all",
            "Simple books only",
            "Most books",
            "Any book"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "English Level Test",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Question ${currentQuestion + 1}/5",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = questions[currentQuestion].first,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        questions[currentQuestion].second.forEach { option ->
            Button(
                onClick = {
                    score += when (option) {
                        "Never", "Not at all", "Basic sentences only" -> 0
                        "Less than 1 year", "Sometimes", "A little", "Simple paragraphs", "Simple books only" -> 1
                        "1-3 years", "Most of the time", "Mostly", "Complex paragraphs", "Most books" -> 2
                        else -> 3
                    }
                    if (currentQuestion < 4) {
                        currentQuestion++
                    } else {
                        onFinishClick(score)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(option)
            }
        }
    }
} 