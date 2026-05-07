package com.alkapa.chiflido.core

/**
 * Evento bruto producido por un Trigger. Cada categoría tiene su variante;
 * agregar un nuevo tipo (p. ej. Scheduled para pagos) sólo requiere otra
 * `data class` en este sealed interface y su `AlertRule` correspondiente.
 */
sealed interface TriggerEvent {
    val alertId: AlertId

    data class Message(
        override val alertId: AlertId,         // = AlertId.forNotification(sbn.key)
        val packageName: String,
        val title: String,
        val text: String,
        val isGroup: Boolean,
    ) : TriggerEvent

    data class Battery(
        override val alertId: AlertId = AlertId.BATTERY_LOW,
        val level: Int,                        // 0..100
        val charging: Boolean,
    ) : TriggerEvent

    // Futuro:
    // data class Scheduled(
    //     override val alertId: AlertId,
    //     val ruleId: Long,
    //     val label: String,
    // ) : TriggerEvent
}
