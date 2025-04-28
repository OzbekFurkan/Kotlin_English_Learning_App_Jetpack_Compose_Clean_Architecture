package com.example.lungoapp.presentation.onboarding

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(
    navController: NavController,
    currentRoute: String,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    Log.d("OnboardingScreen", "Current route: $currentRoute")
    Log.d("OnboardingScreen", "ViewModel hash: ${viewModel.hashCode()}")

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
                viewModel = viewModel,
                onNextClick = {
                    navController.navigate(OnboardingRoutes.ENGLISH_TEST)
                }
            )
        }
        OnboardingRoutes.ENGLISH_TEST -> {
            EnglishTestScreen(
                viewModel = viewModel,
                onFinishClick = {
                    navController.navigate(OnboardingRoutes.TEST_RESULT)
                }
            )
        }
        OnboardingRoutes.TEST_RESULT -> {
            TestResultScreen(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(OnboardingRoutes.LOGIN) {
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