package com.alkapa.chiflido.core

/** Origen del trigger; se usa para iconos y agrupación visual. */
enum class TriggerSource { MESSAGING, BATTERY, SCHEDULED }

/**
 * Datos que el AlertEngine necesita para dibujar la notificación de
 * foreground y elegir el patrón de vibración.
 */
data class AlertSpec(
    val title: String,
    val body: String,
    val severity: Int,                  // 1..5; controla el patrón de vibración
    val source: TriggerSource,
)
