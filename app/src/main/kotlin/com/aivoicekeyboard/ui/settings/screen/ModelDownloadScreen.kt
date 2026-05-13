package com.aivoicekeyboard.ui.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicekeyboard.ui.settings.viewmodel.SettingsViewModel

@Composable
fun ModelDownloadScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val modelStatus by viewModel.modelStatus.collectAsState()
    var progress by remember { mutableFloatStateOf(0f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Speech-to-Text Model", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        when (val status = modelStatus) {
            is SettingsViewModel.ModelStatus.Ready -> {
                Text("Model is downloaded and ready", color = MaterialTheme.colorScheme.primary)
            }
            is SettingsViewModel.ModelStatus.NotDownloaded -> {
                Text("Model not downloaded yet")
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.downloadModel(
                        onProgress = { progress = it },
                        onComplete = {}
                    )
                }) {
                    Text("Download Model")
                }
            }
            is SettingsViewModel.ModelStatus.Downloading -> {
                Text("Downloading...")
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            is SettingsViewModel.ModelStatus.Error -> {
                Text("Error: ${status.message}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.downloadModel(
                        onProgress = { progress = it },
                        onComplete = {}
                    )
                }) {
                    Text("Retry")
                }
            }
            is SettingsViewModel.ModelStatus.Unknown -> {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
    }
}
