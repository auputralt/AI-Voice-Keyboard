package com.aivoicekeyboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_triggers")
data class AppTrigger(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val modeId: String
)
