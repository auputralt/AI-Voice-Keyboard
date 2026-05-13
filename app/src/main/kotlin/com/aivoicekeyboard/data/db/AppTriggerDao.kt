package com.aivoicekeyboard.data.db

import androidx.room.*
import com.aivoicekeyboard.data.model.AppTrigger
import kotlinx.coroutines.flow.Flow

@Dao
interface AppTriggerDao {
    @Query("SELECT * FROM app_triggers")
    fun getAllTriggers(): Flow<List<AppTrigger>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trigger: AppTrigger)

    @Delete
    suspend fun delete(trigger: AppTrigger)
}
