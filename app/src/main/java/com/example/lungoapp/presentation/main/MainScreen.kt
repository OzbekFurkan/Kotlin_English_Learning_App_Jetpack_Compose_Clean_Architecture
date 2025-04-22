package com.example.lungoapp.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lungoapp.presentation.bookmarks.BookmarksScreen
import com.example.lungoapp.presentation.home.HomeScreen
import com.example.lungoapp.presentation.navigation.BottomNavItem
import com.example.lungoapp.presentation.profile.ProfileScreen
import com.example.lungoapp.presentation.practice.PracticeModeScreen
import com.example.lungoapp.presentation.practice.vocabulary.VocabularyQuizScreen
import com.example.lungoapp.presentation.practice.listening.ListeningQuizScreen
import com.example.lungoapp.presentation.practice.reading.ReadingPracticeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavController
) {
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookmarks,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route?.split("/")?.firstOrNull()

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
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
                    HomeScreen(bottomNavController)
                }
                composable(BottomNavItem.Bookmarks.route) {
                    BookmarksScreen(bottomNavController)
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
                    PracticeModeScreen(
                        navController = bottomNavController,
                        practiceMode = mode
                    )
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
                    when (mode) {
                        "vocabulary" -> VocabularyQuizScreen(navController = bottomNavController)
                        "listening" -> ListeningQuizScreen(
                            onNavigateBack = { bottomNavController.navigateUp() }
                        )
                        "reading" -> ReadingPracticeScreen(
                            onNavigateBack = { bottomNavController.navigateUp() }
                        )
                        else -> Text("Practice $mode - $topic")
                    }
                }
            }
        }
    }
} 