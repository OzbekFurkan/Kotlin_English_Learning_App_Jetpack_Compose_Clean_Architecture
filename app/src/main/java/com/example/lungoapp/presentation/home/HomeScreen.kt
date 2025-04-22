package com.example.lungoapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lungo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF5722)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF5722)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // XP Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "XP",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        LinearProgressIndicator(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            progress = { 0.7f },
                            color = Color.Green,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "12 lvl",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(value = "5+", label = "Days", icon = Icons.Default.DateRange)
                        StatItem(value = "29", label = "Likes", icon = Icons.Default.ThumbUp)
                        StatItem(value = "B2", label = "Level", icon = Icons.Default.Star)
                        StatItem(value = "11", label = "Saved", icon = Icons.Default.Favorite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Learning Options Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LearningOptionCard(
                    title = "Vocabulary",
                    icon = Icons.Default.Email,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("practice/vocabulary/serbest") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                LearningOptionCard(
                    title = "Speaking",
                    icon = Icons.Default.Call,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("practice/speaking") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LearningOptionCard(
                    title = "Listening",
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("practice/listening") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                LearningOptionCard(
                    title = "Reading",
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("practice/reading") }
                )
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningOptionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF5722)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
} 