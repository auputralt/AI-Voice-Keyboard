package com.aivoicekeyboard.data.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aivoicekeyboard.data.model.AppTrigger
import com.aivoicekeyboard.data.model.CustomMode

@Database(
    entities = [CustomMode::class, AppTrigger::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customModeDao(): CustomModeDao
    abstract fun appTriggerDao(): AppTriggerDao

    class SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                """INSERT INTO custom_modes (id, name, prompt, description, isDefault, temperature, maxTokens)
                   VALUES ('default', 'Default', 'Rewrite the following text with correct grammar and spelling. Return only the corrected text, nothing else.', 'Standard correction mode', 1, 0.7, 256)"""
            )
            db.execSQL(
                """INSERT INTO custom_modes (id, name, prompt, description, isDefault, temperature, maxTokens)
                   VALUES ('formal', 'Formal', 'Rewrite the following text in a formal, professional tone. Return only the rewritten text.', 'Professional/business tone', 0, 0.5, 256)"""
            )
            db.execSQL(
                """INSERT INTO custom_modes (id, name, prompt, description, isDefault, temperature, maxTokens)
                   VALUES ('casual', 'Casual', 'Rewrite the following text in a casual, friendly tone. Return only the rewritten text.', 'Relaxed/conversational tone', 0, 0.8, 256)"""
            )
        }
    }
}
