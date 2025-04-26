package com.example.lungoapp.presentation.practice.speaking

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lungoapp.ui.components.ClickableWord
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingPracticeScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpeakingPracticeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            Toast.makeText(context, "Microphone permission is required for speaking practice", Toast.LENGTH_LONG).show()
        }
    }

    // Check and request permission
    fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startListening()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speaking Practice") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearMessages()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        modifier = Modifier.padding(vertical = 4.dp),
                        onSaveWord = { word -> 
                            coroutineScope.launch {
                                viewModel.saveBookmark(word)
                            }
                        }
                    )
                }
            }

            // Record button
            Button(
                onClick = {
                    if (isListening) {
                        viewModel.stopListening()
                    } else {
                        checkAndRequestPermission()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isListening) "Stop Recording" else "Start Recording"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isListening) "Stop Recording" else "Start Recording",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier,
    onSaveWord: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 280.dp),
            shape = MaterialTheme.shapes.medium,
            color = if (message.isUser) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Split message into words and make each word clickable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    message.text.split(" ").forEach { word ->
                        ClickableWord(
                            word = word,
                            onSave = { onSaveWord(word) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
} 