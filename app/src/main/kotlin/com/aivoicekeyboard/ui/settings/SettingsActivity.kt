package com.aivoicekeyboard.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aivoicekeyboard.ui.settings.navigation.SettingsNavGraph
import com.aivoicekeyboard.ui.theme.AiKeyboardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiKeyboardTheme { SettingsNavGraph() }
        }
    }
}
