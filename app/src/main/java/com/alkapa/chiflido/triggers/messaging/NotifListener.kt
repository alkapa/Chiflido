package com.alkapa.chiflido.triggers.messaging

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.alkapa.chiflido.ChiflidoApp

/**
 * Listener de notificaciones del sistema. En el paso 5 sólo loguea: nos
 * permite verificar manualmente que el permiso está concedido y que las
 * notificaciones llegan al servicio. La lógica de matching y disparo se
 * conecta en el paso 7+.
 */
class NotifListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in TargetApp.allKnownPackages) return
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        Log.d(
            ChiflidoApp.TAG,
            "notif POSTED key=${sbn.key} pkg=${sbn.packageName} title=$title text=$text",
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName !in TargetApp.allKnownPackages) return
        Log.d(ChiflidoApp.TAG, "notif REMOVED key=${sbn.key} pkg=${sbn.packageName}")
    }
}
