package com.example.lungoapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    
    object Bookmarks : BottomNavItem(
        route = "bookmarks",
        title = "Bookmarks",
        icon = Icons.Default.Favorite
    )
    
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.AccountCircle
    )
} 