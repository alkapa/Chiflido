package com.alkapa.chiflido.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Única fila (id = 0) con los ajustes del trigger de batería.
 */
@Entity(tableName = "battery_settings")
data class BatterySettings(
    @PrimaryKey val id: Int = 0,
    val thresholdPct: Int = 20,    // 5..50
    val enabled: Boolean = true,
)
