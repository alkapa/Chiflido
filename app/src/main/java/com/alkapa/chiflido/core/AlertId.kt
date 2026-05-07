package com.alkapa.chiflido.core

/**
 * Identificador único de una alerta activa. Para mensajería usamos el
 * `StatusBarNotification.key`; para batería un valor constante; para alertas
 * programadas (futuro) un sufijo con el id de la regla.
 */
@JvmInline
value class AlertId(val raw: String) {
    companion object {
        val BATTERY_LOW = AlertId("battery_low")
        fun forNotification(key: String) = AlertId("notif:$key")
        fun forScheduled(ruleId: Long) = AlertId("scheduled:$ruleId")
    }
}
