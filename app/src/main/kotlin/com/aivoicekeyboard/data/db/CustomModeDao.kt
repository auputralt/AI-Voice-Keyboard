package com.aivoicekeyboard.data.db

import androidx.room.*
import com.aivoicekeyboard.data.model.CustomMode
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomModeDao {
    @Query("SELECT * FROM custom_modes")
    fun getAllModes(): Flow<List<CustomMode>>

    @Query("SELECT * FROM custom_modes WHERE id = :id")
    suspend fun getModeById(id: String): CustomMode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mode: CustomMode)

    @Update
    suspend fun update(mode: CustomMode)

    @Delete
    suspend fun delete(mode: CustomMode)

    @Query("UPDATE custom_modes SET isDefault = 0")
    suspend fun clearAllDefaults()
}
