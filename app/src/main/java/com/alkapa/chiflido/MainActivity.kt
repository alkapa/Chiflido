package com.alkapa.chiflido

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.alkapa.chiflido.data.WatchedContact
import com.alkapa.chiflido.triggers.messaging.TargetApp
import com.alkapa.chiflido.ui.home.HomeScreen
import com.alkapa.chiflido.ui.messaging.MessagingViewModel
import com.alkapa.chiflido.ui.theme.ChiflidoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val messagingViewModel: MessagingViewModel by viewModels()

    /**
     * Si el contact picker devolvió una Uri pero todavía no teníamos
     * READ_CONTACTS, la guardamos para procesarla cuando el usuario conceda
     * el permiso.
     */
    private var pendingContactUri: Uri? = null

    private val pickContactLauncher = registerForActivityResult(
        ActivityResultContracts.PickContact(),
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        if (hasReadContacts()) {
            insertContactFromUri(uri)
        } else {
            pendingContactUri = uri
            requestReadContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private val requestReadContactsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val pending = pendingContactUri ?: return@registerForActivityResult
        pendingContactUri = null
        if (granted) insertContactFromUri(pending)
    }

    private val requestPostNotifLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* no-op: si el usuario rechaza, igual abre la app */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ChiflidoApp

        // Android 13+: pedir POST_NOTIFICATIONS al primer arranque.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPostNotifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            ChiflidoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val serviceEnabled by app.settings.serviceEnabled.collectAsState(initial = true)
                    var notifGranted by remember { mutableStateOf(isNotifListenerGranted()) }

                    HomeScreen(
                        messagingViewModel = messagingViewModel,
                        serviceEnabled = serviceEnabled,
                        onServiceEnabledChange = { enabled ->
                            lifecycleScope.launch { app.settings.setServiceEnabled(enabled) }
                        },
                        onAddContact = { pickContactLauncher.launch(null) },
                        notifListenerGranted = notifGranted,
                        onRequestNotifListener = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            notifGranted = isNotifListenerGranted()
                        },
                    )
                }
            }
        }
    }

    private fun hasReadContacts(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Lee el display name del contacto seleccionado y lo inserta como nuevo
     * `WatchedContact` con valores por defecto. El usuario afina app, umbral
     * y ignoreGroups directo desde la lista (ContactItem).
     */
    private fun insertContactFromUri(uri: Uri) {
        val displayName = readDisplayName(uri) ?: run {
            Log.w(ChiflidoApp.TAG, "no se pudo leer DISPLAY_NAME de $uri")
            return
        }
        messagingViewModel.addContact(
            WatchedContact(
                name = displayName,
                packageName = TargetApp.WHATSAPP.packageName,    // default sensato
                minImportance = 3,
                ignoreGroups = true,
                enabled = true,
            ),
        )
    }

    private fun readDisplayName(uri: Uri): String? {
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                if (idx >= 0) return cursor.getString(idx)
            }
        }
        return null
    }

    private fun isNotifListenerGranted(): Boolean {
        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this)
        return packageName in enabled
    }
}
