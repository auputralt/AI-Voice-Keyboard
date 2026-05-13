package com.aivoicekeyboard.ui.settings.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsHomeScreen(
    onApiKeyClick: () -> Unit,
    onModesClick: () -> Unit,
    onModelClick: () -> Unit,
    onTriggersClick: () -> Unit,
    onCreateModeClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        SettingItem("API Key", "Configure your OpenRouter API key", onApiKeyClick)
        SettingItem("Speech Model", "Download STT model for offline use", onModelClick)
        SettingItem("App Triggers", "Link apps to specific modes", onTriggersClick)
        SettingItem("Create Mode", "Add a new text processing mode", onCreateModeClick)
    }
}

@Composable
private fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
