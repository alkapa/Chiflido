package com.alkapa.chiflido.triggers.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alkapa.chiflido.ChiflidoApp

/**
 * Re-arranca el monitor de batería tras un reboot. Sin esto, el receiver
 * registrado en runtime se pierde.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext as ChiflidoApp
        app.batteryMonitor.start(app.appScope)
    }
}
