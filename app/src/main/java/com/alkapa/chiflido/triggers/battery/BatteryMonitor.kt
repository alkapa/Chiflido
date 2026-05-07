package com.alkapa.chiflido.triggers.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.alkapa.chiflido.core.Trigger
import kotlinx.coroutines.CoroutineScope

/**
 * Registra/desregistra el `BatteryReceiver` en runtime. Vivimos en el
 * proceso de la app; lo arrancamos desde `ChiflidoApp.onCreate()` y desde
 * `BootReceiver` después de un reboot.
 *
 * El receiver se registra con `RECEIVER_NOT_EXPORTED` (Android 13+ lo
 * exige incluso para system broadcasts cuando los registramos en runtime).
 */
class BatteryMonitor(private val appContext: Context) : Trigger {

    private val receiver = BatteryReceiver()
    private var registered = false

    override fun start(scope: CoroutineScope) {
        if (registered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            // BATTERY_CHANGED se omite a propósito: es muy ruidoso. Se
            // habilita en una iteración futura cuando el threshold del
            // usuario sea > 15% (porque entonces ACTION_BATTERY_LOW no
            // dispara). Para MVP usamos sólo eventos system-defined.
        }
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        registered = true
    }

    override fun stop() {
        if (!registered) return
        runCatching { appContext.unregisterReceiver(receiver) }
        registered = false
    }
}
