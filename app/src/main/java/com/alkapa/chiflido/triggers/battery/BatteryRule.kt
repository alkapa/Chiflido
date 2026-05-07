package com.alkapa.chiflido.triggers.battery

import com.alkapa.chiflido.core.AlertRule
import com.alkapa.chiflido.core.AlertSpec
import com.alkapa.chiflido.core.TriggerEvent
import com.alkapa.chiflido.core.TriggerSource
import com.alkapa.chiflido.data.BatterySettings

/**
 * Regla de batería:
 *  - Si está cargando o el nivel es mayor al umbral: NO disparar.
 *  - Severity en función del nivel (más bajo = más severo).
 */
class BatteryRule(
    private val loadSettings: suspend () -> BatterySettings,
) : AlertRule<TriggerEvent.Battery> {

    override suspend fun evaluate(event: TriggerEvent.Battery): AlertSpec? {
        val settings = loadSettings()
        if (!settings.enabled) return null
        if (event.charging) return null
        if (event.level > settings.thresholdPct) return null

        val sev = severityFor(event.level)
        return AlertSpec(
            title = "Batería al ${event.level}%",
            body = "Conecta el cargador",
            severity = sev,
            source = TriggerSource.BATTERY,
        )
    }

    companion object {
        fun severityFor(level: Int): Int = when {
            level <= 5 -> 5
            level <= 10 -> 4
            level <= 15 -> 3
            level <= 20 -> 2
            else -> 1
        }
    }
}
