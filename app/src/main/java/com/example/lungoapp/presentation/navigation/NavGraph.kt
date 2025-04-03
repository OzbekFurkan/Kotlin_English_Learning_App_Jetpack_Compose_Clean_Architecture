package com.example.lungoapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lungoapp.presentation.auth.LoginScreen
import com.example.lungoapp.presentation.auth.RegisterScreen
import com.example.lungoapp.presentation.main.MainScreen
import com.example.lungoapp.presentation.onboarding.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = OnboardingRoutes.WELCOME
    ) {
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
        
        composable(
            route = "${OnboardingRoutes.TEST_RESULT}/{englishLevel}",
            arguments = listOf(
                androidx.navigation.navArgument("englishLevel") {
                    type = androidx.navigation.NavType.StringType
                    require(true)
                }
            )
        ) { backStackEntry ->
            val englishLevel = backStackEntry.arguments?.getString("englishLevel") ?: "Beginner"
            TestResultScreen(
                navController = navController,
                englishLevel = englishLevel
            )
        }
        
        composable(OnboardingRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(OnboardingRoutes.HOME) {
                        popUpTo(OnboardingRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { englishLevel ->
                    navController.navigate("${OnboardingRoutes.REGISTER}/$englishLevel")
                }
            )
        }
        
        composable(
            route = "${OnboardingRoutes.REGISTER}/{englishLevel}",
            arguments = listOf(
                androidx.navigation.navArgument("englishLevel") {
                    type = androidx.navigation.NavType.StringType
                    require(true)
                }
            )
        ) { backStackEntry ->
            val englishLevel = backStackEntry.arguments?.getString("englishLevel") ?: "Beginner"
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(OnboardingRoutes.HOME) {
                        popUpTo(OnboardingRoutes.REGISTER) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(OnboardingRoutes.LOGIN)
                },
                englishLevel = englishLevel
            )
        }
        
        composable(OnboardingRoutes.HOME) {
            MainScreen()
        }
    }
} 