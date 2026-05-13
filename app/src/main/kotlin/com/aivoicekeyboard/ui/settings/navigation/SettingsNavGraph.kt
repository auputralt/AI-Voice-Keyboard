package com.aivoicekeyboard.ui.settings.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aivoicekeyboard.ui.settings.screen.*

object Routes {
    const val HOME = "home"
    const val API_KEY = "api_key"
    const val MODE_EDIT = "mode_edit/{modeId}"
    const val MODE_CREATE = "mode_create"
    const val MODEL_DOWNLOAD = "model_download"
    const val APP_TRIGGERS = "app_triggers"

    fun modeEdit(modeId: String) = "mode_edit/$modeId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNavGraph() {
    val navController = rememberNavController()
    var title by remember { mutableStateOf("AI Voice Keyboard") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                title = "AI Voice Keyboard"
                SettingsHomeScreen(
                    onApiKeyClick = { navController.navigate(Routes.API_KEY) },
                    onModesClick = { /* list modes */ },
                    onModelClick = { navController.navigate(Routes.MODEL_DOWNLOAD) },
                    onTriggersClick = { navController.navigate(Routes.APP_TRIGGERS) },
                    onCreateModeClick = { navController.navigate(Routes.MODE_CREATE) }
                )
            }
            composable(Routes.API_KEY) {
                title = "API Key"
                ApiKeyScreen()
            }
            composable(Routes.MODEL_DOWNLOAD) {
                title = "Speech Model"
                ModelDownloadScreen()
            }
            composable(Routes.APP_TRIGGERS) {
                title = "App Triggers"
                AppTriggersScreen()
            }
            composable(
                Routes.MODE_EDIT,
                arguments = listOf(navArgument("modeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val modeId = backStackEntry.arguments?.getString("modeId") ?: ""
                title = "Edit Mode"
                ModeEditScreen(modeId = modeId)
            }
            composable(Routes.MODE_CREATE) {
                title = "New Mode"
                ModeEditScreen(modeId = null)
            }
        }
    }
}
