package com.alkapa.chiflido.triggers.messaging

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.alkapa.chiflido.ChiflidoApp
import com.alkapa.chiflido.classifier.HeuristicClassifier
import com.alkapa.chiflido.core.AlertId
import com.alkapa.chiflido.core.TriggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * NotificationListenerService de Chiflido. En cada notificación posted:
 *  1. Filtra por TargetApp.
 *  2. Construye TriggerEvent.Message.
 *  3. Pasa por MessageRule (que consulta el DAO + classifier).
 *  4. Si la regla devuelve AlertSpec, llama AlertEngine.fire().
 */
class NotifListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val rule: MessageRule by lazy {
        val app = applicationContext as ChiflidoApp
        MessageRule(
            classifier = HeuristicClassifier(),
            loadEnabledContacts = { app.database.contactDao().getEnabled() },
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in TargetApp.allKnownPackages) return
        val app = applicationContext as ChiflidoApp

        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val isGroup = sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0 ||
            extras.getBooleanCompat(EXTRA_IS_GROUP_CONVERSATION)

        val event = TriggerEvent.Message(
            alertId = AlertId.forNotification(sbn.key),
            packageName = sbn.packageName,
            title = title,
            text = text,
            isGroup = isGroup,
        )

        scope.launch {
            val spec = rule.evaluate(event) ?: return@launch
            Log.d(ChiflidoApp.TAG, "match key=${sbn.key} sev=${spec.severity}")
            app.alertEngine.fire(event.alertId, spec)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName !in TargetApp.allKnownPackages) return
        val app = applicationContext as ChiflidoApp
        app.alertEngine.clear(AlertId.forNotification(sbn.key))
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun Bundle.getBooleanCompat(key: String): Boolean = try {
        getBoolean(key, false)
    } catch (_: Throwable) {
        false
    }

    private companion object {
        // Heurística adicional: WhatsApp marca conversaciones de grupo con
        // este extra cuando aplica.
        const val EXTRA_IS_GROUP_CONVERSATION = "android.isGroupConversation"
    }
}
