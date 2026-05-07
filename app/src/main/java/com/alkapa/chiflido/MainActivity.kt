package com.alkapa.chiflido

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.alkapa.chiflido.ui.home.HomeScreen
import com.alkapa.chiflido.ui.messaging.MessagingViewModel
import com.alkapa.chiflido.ui.theme.ChiflidoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val messagingViewModel: MessagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ChiflidoApp

        setContent {
            ChiflidoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val serviceEnabled by app.settings.serviceEnabled.collectAsState(initial = true)
                    // Re-evalúa el permiso cada vez que la activity se recompone
                    // tras volver de los Settings.
                    var notifGranted by remember { mutableStateOf(isNotifListenerGranted()) }

                    HomeScreen(
                        messagingViewModel = messagingViewModel,
                        serviceEnabled = serviceEnabled,
                        onServiceEnabledChange = { enabled ->
                            lifecycleScope.launch { app.settings.setServiceEnabled(enabled) }
                        },
                        onAddContact = { /* paso 7 */ },
                        notifListenerGranted = notifGranted,
                        onRequestNotifListener = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            // Tras un round-trip a Settings, re-leer el estado.
                            notifGranted = isNotifListenerGranted()
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Trigger recomposición forzando lectura del estado en setContent (vía remember).
    }

    private fun isNotifListenerGranted(): Boolean {
        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this)
        return packageName in enabled
    }
}
