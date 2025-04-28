package com.example.lungoapp.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lungoapp.R

@Composable
fun EnglishTestScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinishClick: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var isPredicting by remember { mutableStateOf(false) }
    val cefrLevel by viewModel.cefrLevel.collectAsState()

    val aiPassage = """
With the rapid advancement of technology, artificial intelligence (AI) has become an integral part of our daily lives. From education to healthcare, and agriculture to the financial sector, AI's influence is undeniable. For instance, in education, personalized learning programs adapt to the strengths and weaknesses of students, making the learning process more effective. However, this rapid transformation also raises ethical concerns. Issues like data privacy and the changes in the job market are two critical topics that require careful consideration.
""".trimIndent()

    val questions = listOf(
        Triple(
            "What color is the sky?",
            listOf("Green", "Blue", "Red", "Yellow"),
            1 // Blue
        ),
        Triple(
            "I ____ a student. (boşluğa uygun kelimeyi seçiniz)",
            listOf("am", "is", "are", "be"),
            0 // am
        ),
        Triple(
            "\"I like to eat pizza.\" cümlesi 'Ben pizza yemeyi severim.' anlamına gelir mi?",
            listOf("Doğru", "Yanlış"),
            0 // Doğru
        ),
        Triple(
            "Hangi kelime bu resmi tanımlar?",
            listOf("Apple", "Banana", "Orange", "All of them"),
            3 // All of them
        ),
        Triple(
            "I ____ to the cinema last night. I saw a great movie.",
            listOf("go", "went", "have gone", "will go"),
            1 // went
        ),
        Triple(
            "Read the following text and answer the question:\n\nLast weekend, I went camping with my friends. We cooked food over a campfire, told stories, and sang songs. It was a lot of fun.\n\nWhat did they do at the campsite?",
            listOf(
                "They went shopping.",
                "They played video games.",
                "They enjoyed outdoor activities.",
                "They stayed at home."
            ),
            2 // They enjoyed outdoor activities.
        ),
        Triple(
            "If I had more money ____",
            listOf(
                "I will buy a car.",
                "I would travel around the world.",
                "I can buy a house.",
                "I traveled to Paris."
            ),
            1 // I would travel around the world.
        ),
        Triple(
            "How does artificial intelligence benefit education, according to the passage?",
            listOf(
                "By replacing teachers with AI systems",
                "By creating learning programs tailored to students' strengths and weaknesses",
                "By focusing only on students' weaknesses",
                "By automating all school-related activities"
            ),
            1 // By creating learning programs tailored to students' strengths and weaknesses
        ),
        Triple(
            "What is one ethical concern related to artificial intelligence mentioned in the passage?",
            listOf(
                "Lack of funding for AI projects",
                "Overuse of AI in entertainment",
                "Data privacy and its implications",
                "AI's inability to make decisions"
            ),
            2 // Data privacy and its implications
        ),
        Triple(
            "In which sector(s) does the passage highlight the impact of artificial intelligence?",
            listOf(
                "Tourism and healthcare",
                "Education",
                "Entertainment and sports",
                "Manufacturing and retail"
            ),
            1 // Education
        )
    )

    // Effect to handle navigation when prediction is complete
    LaunchedEffect(cefrLevel) {
        if (isPredicting && cefrLevel != null) {
            isPredicting = false
            onFinishClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Question counter
        Text(
            text = "Question ${currentQuestion + 1}/${questions.size}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Question text
        Text(
            text = questions[currentQuestion].first,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Options
        questions[currentQuestion].second.forEachIndexed { index, option ->
            OutlinedButton(
                onClick = {
                    selectedOption = index
                    viewModel.updateQuestionAnswer(currentQuestion, index)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (selectedOption == index) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentQuestion > 0) {
                Button(
                    onClick = { 
                        currentQuestion--
                        selectedOption = -1
                    }
                ) {
                    Text("Previous")
                }
            }

            Button(
                onClick = {
                    if (currentQuestion < questions.size - 1) {
                        currentQuestion++
                        selectedOption = -1
                    } else {
                        // Last question completed, predict CEFR level
                        isPredicting = true
                        viewModel.predictCefrLevel()
                    }
                },
                enabled = selectedOption != -1
            ) {
                Text(if (currentQuestion < questions.size - 1) "Next" else "Finish")
            }
        }
    }
} 