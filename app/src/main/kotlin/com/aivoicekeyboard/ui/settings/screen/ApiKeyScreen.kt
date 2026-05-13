package com.aivoicekeyboard.ui.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicekeyboard.ui.settings.viewmodel.SettingsViewModel

@Composable
fun ApiKeyScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val apiKeyState by viewModel.apiKeyState.collectAsState()
    var keyInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            label = { Text("OpenRouter API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.saveApiKey(keyInput) },
                enabled = keyInput.isNotBlank()
            ) {
                Text("Save")
            }
            OutlinedButton(onClick = { viewModel.validateApiKey() }) {
                Text("Validate")
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val state = apiKeyState) {
            is SettingsViewModel.ApiKeyState.Valid -> Text("API key is set and valid", color = MaterialTheme.colorScheme.primary)
            is SettingsViewModel.ApiKeyState.Missing -> Text("No API key configured", color = MaterialTheme.colorScheme.error)
            is SettingsViewModel.ApiKeyState.Checking -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
            is SettingsViewModel.ApiKeyState.Invalid -> Text("Invalid: ${state.reason}", color = MaterialTheme.colorScheme.error)
            is SettingsViewModel.ApiKeyState.Unknown -> {}
        }
    }
}
