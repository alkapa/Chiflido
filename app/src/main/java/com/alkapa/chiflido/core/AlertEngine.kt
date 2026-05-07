package com.alkapa.chiflido.core

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.alkapa.chiflido.ChiflidoApp
import com.alkapa.chiflido.service.AlertService

/**
 * Punto único por donde pasan TODAS las alertas. Los Triggers nunca llaman
 * directo al AlertService; le pasan el `AlertSpec` aquí y el engine se
 * encarga de empezarlo/pararlo.
 */
class AlertEngine(private val appContext: Context) {

    fun fire(id: AlertId, spec: AlertSpec) {
        Log.i(ChiflidoApp.TAG, "AlertEngine.fire(${id.raw}) sev=${spec.severity} ${spec.title}")
        ContextCompat.startForegroundService(
            appContext,
            AlertService.addIntent(appContext, id, spec),
        )
    }

    fun clear(id: AlertId) {
        Log.i(ChiflidoApp.TAG, "AlertEngine.clear(${id.raw})")
        // startService basta: si el servicio ya no corre, no se levanta.
        runCatching { appContext.startService(AlertService.stopIntent(appContext, id)) }
    }

    fun silenceAll() {
        Log.i(ChiflidoApp.TAG, "AlertEngine.silenceAll()")
        runCatching { appContext.startService(AlertService.silenceIntent(appContext)) }
    }
}
