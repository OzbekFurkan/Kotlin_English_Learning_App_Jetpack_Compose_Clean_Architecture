package com.example.lungoapp.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lungoapp.presentation.auth.LoginScreen
import com.example.lungoapp.presentation.auth.RegisterScreen
import com.example.lungoapp.presentation.main.MainScreen
import com.example.lungoapp.presentation.onboarding.EnglishTestScreen
import com.example.lungoapp.presentation.onboarding.OnboardingRoutes
import com.example.lungoapp.presentation.onboarding.PersonalInfoScreen
import com.example.lungoapp.presentation.onboarding.TestResultScreen
import com.example.lungoapp.presentation.onboarding.WelcomeScreen
import com.example.lungoapp.presentation.practice.PracticeModeScreen
import com.example.lungoapp.presentation.practice.vocabulary.VocabularyQuizScreen
import com.example.lungoapp.presentation.practice.listening.ListeningQuizScreen
import com.example.lungoapp.presentation.practice.reading.ReadingPracticeScreen
import com.example.lungoapp.presentation.practice.speaking.SpeakingPracticeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = OnboardingRoutes.WELCOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding routes
        composable(OnboardingRoutes.WELCOME) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(OnboardingRoutes.PERSONAL_INFO)
                }
            )
        }
        
        composable(OnboardingRoutes.PERSONAL_INFO) {
            PersonalInfoScreen(
                onNextClick = {
                    navController.navigate(OnboardingRoutes.ENGLISH_TEST)
                }
            )
        }
        
        composable(OnboardingRoutes.ENGLISH_TEST) {
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
        
        composable("${OnboardingRoutes.TEST_RESULT}/{englishLevel}") { backStackEntry ->
            val englishLevel = backStackEntry.arguments?.getString("englishLevel") ?: "Beginner"
            TestResultScreen(
                navController = navController,
                englishLevel = englishLevel,
                onLoginClick = {
                    navController.navigate(OnboardingRoutes.LOGIN)
                },
                onRegisterClick = {
                    navController.navigate(OnboardingRoutes.REGISTER)
                }
            )
        }
        
        // Auth routes
        composable(OnboardingRoutes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(OnboardingRoutes.HOME) {
                        popUpTo(OnboardingRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(OnboardingRoutes.REGISTER)
                }
            )
        }
        
        composable(OnboardingRoutes.REGISTER) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate(OnboardingRoutes.HOME) {
                        popUpTo(OnboardingRoutes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(OnboardingRoutes.LOGIN)
                }
            )
        }
        
        // Main app with bottom navigation
        composable(OnboardingRoutes.HOME) {
            MainScreen(parentNavController = navController)
        }

        // Practice routes
        composable(
            route = "practice/{mode}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: return@composable
            when (mode.lowercase()) {
                "speaking" -> SpeakingPracticeScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
                else -> PracticeModeScreen(
                    navController = navController,
                    practiceMode = mode
                )
            }
        }

        composable(
            route = "practice/{mode}/{topic}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("topic") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: return@composable
            val topic = backStackEntry.arguments?.getString("topic") ?: return@composable
            when (mode.lowercase()) {
                "vocabulary" -> VocabularyQuizScreen(navController = navController)
                "listening" -> ListeningQuizScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
                "reading" -> ReadingPracticeScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
                else -> Text("Practice $mode - $topic")
            }
        }
    }
} 