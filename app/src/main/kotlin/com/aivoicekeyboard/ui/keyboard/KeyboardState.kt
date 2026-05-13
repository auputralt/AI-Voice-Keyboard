package com.aivoicekeyboard.ui.keyboard

sealed class KeyboardState {
    data object Idle : KeyboardState()
    data object Recording : KeyboardState()
    data object Processing : KeyboardState()
    data class Success(val text: String) : KeyboardState()
    data class Error(val message: String, val fallbackText: String? = null) : KeyboardState()
}

data class LastInsertion(
    val rawText: String,
    val processedCharCount: Int
)
