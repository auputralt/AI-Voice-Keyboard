package com.aivoicekeyboard.ui.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aivoicekeyboard.data.model.CustomMode
import com.aivoicekeyboard.ui.settings.viewmodel.SettingsViewModel
import java.util.UUID

@Composable
fun ModeEditScreen(
    modeId: String?,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val modes by viewModel.modes.collectAsState()
    val existingMode = modeId?.let { id -> modes.firstOrNull { it.id == id } }

    var name by remember { mutableStateOf(existingMode?.name ?: "") }
    var prompt by remember { mutableStateOf(existingMode?.prompt ?: "") }
    var description by remember { mutableStateOf(existingMode?.description ?: "") }
    var temperature by remember { mutableFloatStateOf(existingMode?.temperature ?: 0.7f) }
    var maxTokens by remember { mutableIntStateOf(existingMode?.maxTokens ?: 256) }
    var isDefault by remember { mutableStateOf(existingMode?.isDefault ?: false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = prompt, onValueChange = { prompt = it }, label = { Text("System Prompt") },
            modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 6
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text("Temperature: ${String.format("%.1f", temperature)}")
        Slider(value = temperature, onValueChange = { temperature = it }, valueRange = 0f..2f)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = maxTokens.toString(), onValueChange = { maxTokens = it.toIntOrNull() ?: 256 },
            label = { Text("Max Tokens") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Default mode")
            Spacer(Modifier.width(8.dp))
            Switch(checked = isDefault, onCheckedChange = { isDefault = it })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val mode = CustomMode(
                    id = existingMode?.id ?: UUID.randomUUID().toString(),
                    name = name,
                    prompt = prompt,
                    description = description,
                    isDefault = isDefault,
                    temperature = temperature,
                    maxTokens = maxTokens
                )
                viewModel.saveMode(mode)
            },
            enabled = name.isNotBlank() && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (existingMode != null) "Update Mode" else "Create Mode")
        }
    }
}
