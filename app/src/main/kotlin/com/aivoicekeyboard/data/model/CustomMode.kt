package com.aivoicekeyboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_modes")
data class CustomMode(
    @PrimaryKey val id: String,
    val name: String,
    val prompt: String,
    val description: String = "",
    val isDefault: Boolean = false,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 256
)
