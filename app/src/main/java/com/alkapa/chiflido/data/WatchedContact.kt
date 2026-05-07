package com.alkapa.chiflido.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Contacto vigilado para el trigger de mensajería.
 *
 * `name` se compara contra el título de la notificación con `contains`
 * case-insensitive, para sobrevivir formatos de WhatsApp tipo
 * "Juan (3 mensajes)".
 */
@Entity(tableName = "watched_contacts")
data class WatchedContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val packageName: String,
    val minImportance: Int,         // 1..5
    val ignoreGroups: Boolean = true,
    val enabled: Boolean = true,
)
