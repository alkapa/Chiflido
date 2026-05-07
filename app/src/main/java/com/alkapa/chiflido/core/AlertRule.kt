package com.alkapa.chiflido.core

/**
 * Regla que decide si un evento amerita disparar una alerta.
 * Devuelve `null` cuando el evento NO debe disparar nada.
 */
fun interface AlertRule<E : TriggerEvent> {
    suspend fun evaluate(event: E): AlertSpec?
}
