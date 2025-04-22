package com.example.lungoapp.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(
    navController: NavController,
    currentRoute: String
) {
    when (currentRoute) {
        OnboardingRoutes.WELCOME -> {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(OnboardingRoutes.PERSONAL_INFO)
                }
            )
        }
        OnboardingRoutes.PERSONAL_INFO -> {
            PersonalInfoScreen(
                onNextClick = {
                    navController.navigate(OnboardingRoutes.ENGLISH_TEST)
                }
            )
        }
        OnboardingRoutes.ENGLISH_TEST -> {
            EnglishTestScreen(
                onFinishClick = { score ->
                    val level = when {
                        score <= 3 -> "Beginner"
                        score <= 6 -> "Intermediate"
                        score <= 8 -> "Upper Intermediate"
                        else -> "Advanced"
                    }
                    navController.navigate("${OnboardingRoutes.TEST_RESULT}/$level")
                }
            )
        }
        OnboardingRoutes.TEST_RESULT -> {
            val englishLevel = navController.currentBackStackEntry?.arguments?.getString("englishLevel") ?: "Beginner"
            TestResultScreen(
                navController = navController,
                englishLevel = englishLevel,
                onLoginClick = {
                    navController.navigate(OnboardingRoutes.LOGIN) {
                        popUpTo(OnboardingRoutes.WELCOME) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(OnboardingRoutes.REGISTER) {
                        popUpTo(OnboardingRoutes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        else -> {
            // Handle unknown route
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Unknown Route",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(OnboardingRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }}
                ) {
                    Text("Go to Welcome")
                }
            }
        }
    }
}