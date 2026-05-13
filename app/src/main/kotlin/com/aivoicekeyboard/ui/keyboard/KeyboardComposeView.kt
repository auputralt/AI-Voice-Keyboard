package com.aivoicekeyboard.ui.keyboard

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.aivoicekeyboard.ui.theme.AiKeyboardTheme

class KeyboardComposeView(
    context: Context,
    private val controller: KeyboardController,
    private val onSettingsClick: () -> Unit
) : FrameLayout(context) {

    init {
        val composeView = ComposeView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        addView(composeView)

        composeView.setContent {
            AiKeyboardTheme {
                KeyboardContent(controller, onSettingsClick)
            }
        }
    }
}

@Composable
private fun KeyboardContent(
    controller: KeyboardController,
    onSettingsClick: () -> Unit
) {
    val state by controller.state.collectAsState()

    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings button
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }

            // Status display
            Text(
                text = when (state) {
                    is KeyboardState.Idle -> "Tap mic to speak"
                    is KeyboardState.Recording -> "Listening..."
                    is KeyboardState.Processing -> "Processing..."
                    is KeyboardState.Success -> "Done"
                    is KeyboardState.Error -> (state as KeyboardState.Error).message
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )

            // Revert button
            IconButton(
                onClick = { controller.requestRevert() },
                enabled = state is KeyboardState.Success
            ) {
                Icon(Icons.Default.Undo, contentDescription = "Revert")
            }

            // Mic button
            FilledIconButton(
                onClick = {
                    when (state) {
                        is KeyboardState.Idle, is KeyboardState.Error -> controller.startRecording()
                        is KeyboardState.Recording -> controller.cancelRecording()
                        else -> {}
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Record",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
