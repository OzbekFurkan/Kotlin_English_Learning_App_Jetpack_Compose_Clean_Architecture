package com.example.lungoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun ClickableWord(
    word: String,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Text(
        text = word,
        style = MaterialTheme.typography.bodyLarge.copy(
            textDecoration = TextDecoration.Underline
        ),
        modifier = modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        SaveWordDialog(
            word = word,
            onDismiss = { showDialog = false },
            onSave = {
                onSave()
                showDialog = false
            }
        )
    }
} 