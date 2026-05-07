package com.alkapa.chiflido.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.alkapa.chiflido.R
import com.alkapa.chiflido.core.AlertId
import com.alkapa.chiflido.core.AlertSpec
import com.alkapa.chiflido.core.TriggerSource

/**
 * Foreground service que mantiene la vibración mientras haya alertas
 * activas. El estado se modela como Map<AlertId, AlertSpec>; cuando queda
 * vacío, se llama stopSelf().
 */
class AlertService : Service() {

    private val active: MutableMap<String, AlertSpec> = LinkedHashMap()
    private lateinit var vibration: VibrationController

    override fun onCreate() {
        super.onCreate()
        vibration = VibrationController(this)
        ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ADD -> {
                val id = intent.getStringExtra(EXTRA_ID) ?: return START_NOT_STICKY
                val spec = intent.specExtra() ?: return START_NOT_STICKY
                active[id] = spec
                refreshForeground()
                vibration.start(active.values.maxOf { it.severity })
            }
            ACTION_STOP -> {
                val id = intent.getStringExtra(EXTRA_ID) ?: return START_NOT_STICKY
                active.remove(id)
                if (active.isEmpty()) {
                    vibration.stop()
                    stopSelf()
                } else {
                    refreshForeground()
                    vibration.start(active.values.maxOf { it.severity })
                }
            }
            ACTION_SILENCE -> {
                active.clear()
                vibration.stop()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        vibration.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun refreshForeground() {
        val notif = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notif)
        }
    }

    private fun buildNotification(): Notification {
        val title = if (active.size == 1) {
            active.values.first().title
        } else {
            getString(R.string.alert_active_multiple, active.size)
        }
        val body = active.values.take(2).joinToString(separator = " · ") { it.title }

        val silenceIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AlertService::class.java).setAction(ACTION_SILENCE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_lock_silent_mode,
                getString(R.string.alert_silence),
                silenceIntent,
            )
            .build()
    }

    private fun ensureChannel() {
        val nm = getSystemService<NotificationManager>() ?: return
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.alert_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.alert_channel_description)
            // El sonido/vibración del canal queda desactivado; el ruido
            // viene del VibrationController, no de la notif.
            enableVibration(false)
        }
        nm.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "chiflido_alert_channel"
        const val NOTIF_ID = 0xC1F1D0

        const val ACTION_ADD = "com.alkapa.chiflido.action.ADD"
        const val ACTION_STOP = "com.alkapa.chiflido.action.STOP"
        const val ACTION_SILENCE = "com.alkapa.chiflido.action.SILENCE"

        const val EXTRA_ID = "id"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_BODY = "body"
        private const val EXTRA_SEV = "sev"
        private const val EXTRA_SOURCE = "source"

        fun addIntent(context: Context, id: AlertId, spec: AlertSpec): Intent =
            Intent(context, AlertService::class.java)
                .setAction(ACTION_ADD)
                .putExtra(EXTRA_ID, id.raw)
                .putExtra(EXTRA_TITLE, spec.title)
                .putExtra(EXTRA_BODY, spec.body)
                .putExtra(EXTRA_SEV, spec.severity)
                .putExtra(EXTRA_SOURCE, spec.source.name)

        fun stopIntent(context: Context, id: AlertId): Intent =
            Intent(context, AlertService::class.java)
                .setAction(ACTION_STOP)
                .putExtra(EXTRA_ID, id.raw)

        fun silenceIntent(context: Context): Intent =
            Intent(context, AlertService::class.java).setAction(ACTION_SILENCE)

        private fun Intent.specExtra(): AlertSpec? {
            val title = getStringExtra(EXTRA_TITLE) ?: return null
            val body = getStringExtra(EXTRA_BODY) ?: ""
            val sev = getIntExtra(EXTRA_SEV, 1)
            val source = runCatching { TriggerSource.valueOf(getStringExtra(EXTRA_SOURCE) ?: "") }
                .getOrDefault(TriggerSource.MESSAGING)
            return AlertSpec(title, body, sev, source)
        }
    }
}
