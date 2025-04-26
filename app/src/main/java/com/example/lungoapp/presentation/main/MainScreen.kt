package com.example.lungoapp.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lungoapp.presentation.home.HomeScreen
import com.example.lungoapp.presentation.profile.ProfileScreen
import com.example.lungoapp.presentation.practice.PracticeModeScreen
import com.example.lungoapp.presentation.practice.vocabulary.VocabularyQuizScreen
import com.example.lungoapp.presentation.practice.listening.ListeningQuizScreen
import com.example.lungoapp.presentation.practice.reading.ReadingPracticeScreen
import com.example.lungoapp.presentation.practice.speaking.SpeakingPracticeScreen
import com.example.lungoapp.presentation.bookmark.BookmarkScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Bookmarks : BottomNavItem("bookmarks", "Bookmarks", Icons.Default.Favorite)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavController
) {
    val bottomNavController = rememberNavController()
    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookmarks,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(parentNavController)
                }
                composable(BottomNavItem.Bookmarks.route) {
                    BookmarkScreen()
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(bottomNavController)
                }
                
                composable(
                    route = "practice/{mode}",
                    arguments = listOf(
                        navArgument("mode") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val mode = backStackEntry.arguments?.getString("mode") ?: return@composable
                    when (mode.lowercase()) {
                        "speaking" -> SpeakingPracticeScreen(
                            onNavigateBack = { parentNavController.popBackStack() }
                        )
                        else -> PracticeModeScreen(
                            navController = parentNavController,
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
                        "vocabulary" -> VocabularyQuizScreen(navController = parentNavController)
                        "listening" -> ListeningQuizScreen(
                            onNavigateBack = { parentNavController.navigateUp() }
                        )
                        "reading" -> ReadingPracticeScreen(
                            onNavigateBack = { parentNavController.popBackStack() }
                        )
                        else -> Text("Practice $mode - $topic")
                    }
                }
            }
        }
    }
} 