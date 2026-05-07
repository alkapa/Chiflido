package com.alkapa.chiflido.core

import android.content.Context
import android.util.Log
import com.alkapa.chiflido.ChiflidoApp

/**
 * Punto único por donde pasan TODAS las alertas. Los Triggers nunca llaman
 * directo al AlertService; le pasan el `AlertSpec` aquí y el engine se
 * encarga de empezarlo/pararlo.
 *
 * En el paso 4 todavía es log-only; en el paso 9 se conecta a AlertService.
 */
class AlertEngine(private val appContext: Context) {

    fun fire(id: AlertId, spec: AlertSpec) {
        Log.i(ChiflidoApp.TAG, "AlertEngine.fire(${id.raw}) sev=${spec.severity} ${spec.title}")
        // Paso 9: ContextCompat.startForegroundService(appContext, AlertService.addIntent(id, spec))
    }

    fun clear(id: AlertId) {
        Log.i(ChiflidoApp.TAG, "AlertEngine.clear(${id.raw})")
        // Paso 9: appContext.startService(AlertService.stopIntent(id))
    }

    fun silenceAll() {
        Log.i(ChiflidoApp.TAG, "AlertEngine.silenceAll()")
        // Paso 9: appContext.startService(AlertService.silenceIntent())
    }
}
