package com.aivoicekeyboard.ui.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicekeyboard.data.model.AppTrigger
import com.aivoicekeyboard.ui.settings.viewmodel.SettingsViewModel

@Composable
fun AppTriggersScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val triggers by viewModel.triggers.collectAsState()
    var packageName by remember { mutableStateOf("") }
    var modeId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("App Triggers", style = MaterialTheme.typography.titleLarge)
        Text("Link specific apps to processing modes", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text("Package Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = modeId,
                onValueChange = { modeId = it },
                label = { Text("Mode ID") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (packageName.isNotBlank() && modeId.isNotBlank()) {
                    viewModel.saveTrigger(packageName, modeId)
                    packageName = ""
                    modeId = ""
                }
            },
            enabled = packageName.isNotBlank() && modeId.isNotBlank()
        ) {
            Text("Add Trigger")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(triggers) { trigger: AppTrigger ->
                TriggerRow(trigger, onDelete = { viewModel.deleteTrigger(trigger) })
            }
        }
    }
}

@Composable
private fun TriggerRow(trigger: AppTrigger, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(trigger.packageName, style = MaterialTheme.typography.bodyMedium)
                Text("Mode: ${trigger.modeId}", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onDelete) { Text("Delete", color = MaterialTheme.colorScheme.error) }
        }
    }
}
