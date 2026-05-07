package com.alkapa.chiflido.triggers.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.alkapa.chiflido.ChiflidoApp
import com.alkapa.chiflido.core.AlertId
import com.alkapa.chiflido.core.TriggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver para los eventos de batería del sistema:
 *  - ACTION_BATTERY_LOW          → emite evento con level≈15% y evalúa.
 *  - ACTION_BATTERY_OKAY         → limpia la alerta de batería.
 *  - ACTION_POWER_CONNECTED      → limpia la alerta de batería.
 *  - ACTION_BATTERY_CHANGED      → opcional, sólo si el usuario configuró
 *                                  un threshold > 15 (caso en que el sistema
 *                                  no dispara LOW por sí solo).
 */
class BatteryReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as ChiflidoApp
        when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> emitBatteryEvent(context, intent, fallbackLevel = 15)
            Intent.ACTION_BATTERY_OKAY,
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(ChiflidoApp.TAG, "battery cleared by ${intent.action}")
                app.alertEngine.clear(AlertId.BATTERY_LOW)
            }
            Intent.ACTION_BATTERY_CHANGED -> emitBatteryEvent(context, intent, fallbackLevel = 100)
        }
    }

    private fun emitBatteryEvent(context: Context, intent: Intent, fallbackLevel: Int) {
        val app = context.applicationContext as ChiflidoApp
        val level = intent.batteryLevelPercent() ?: fallbackLevel
        val charging = intent.isCharging()
        val event = TriggerEvent.Battery(level = level, charging = charging)
        val rule = BatteryRule(loadSettings = { app.database.batteryDao().get() })

        scope.launch {
            if (!app.settings.serviceEnabled.firstValueOrTrue()) return@launch
            val spec = rule.evaluate(event) ?: run {
                // Si no aplica (por ejemplo, ya está cargando), aseguramos
                // que cualquier alerta previa quede limpia.
                app.alertEngine.clear(AlertId.BATTERY_LOW)
                return@launch
            }
            app.alertEngine.fire(AlertId.BATTERY_LOW, spec)
        }
    }
}

/**
 * Helper extensiones del Intent de batería.
 */
private fun Intent.batteryLevelPercent(): Int? {
    val level = getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    if (level < 0 || scale <= 0) return null
    return (level * 100f / scale).toInt()
}

private fun Intent.isCharging(): Boolean {
    val status = getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    return status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL
}

private suspend fun Flow<Boolean>.firstValueOrTrue(): Boolean =
    runCatching { first() }.getOrDefault(true)
