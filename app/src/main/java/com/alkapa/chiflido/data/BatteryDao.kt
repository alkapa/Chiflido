package com.alkapa.chiflido.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface BatteryDao {
    @Query("SELECT * FROM battery_settings WHERE id = 0")
    fun observeRaw(): Flow<BatterySettings?>

    /** Devuelve siempre un valor: si la fila no existe usa el default. */
    fun observe(): Flow<BatterySettings> =
        observeRaw().map { it ?: BatterySettings() }

    @Query("SELECT * FROM battery_settings WHERE id = 0")
    suspend fun getRaw(): BatterySettings?

    suspend fun get(): BatterySettings = getRaw() ?: BatterySettings()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: BatterySettings)
}
