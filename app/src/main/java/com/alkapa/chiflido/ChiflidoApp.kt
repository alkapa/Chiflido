package com.alkapa.chiflido

import android.app.Application
import com.alkapa.chiflido.core.AlertEngine
import com.alkapa.chiflido.data.AppDatabase
import com.alkapa.chiflido.data.AppSettings
import com.alkapa.chiflido.triggers.battery.BatteryMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

/**
 * Application principal. Expone como singletons la base de datos Room, los
 * settings, el AlertEngine y el monitor de batería. Reacciona al switch
 * global encendiendo/apagando triggers.
 */
class ChiflidoApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val settings: AppSettings by lazy { AppSettings(this) }
    val alertEngine: AlertEngine by lazy { AlertEngine(this) }
    val batteryMonitor: BatteryMonitor by lazy { BatteryMonitor(this) }

    val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Reacciona al switch global: enciende/apaga triggers.
        settings.serviceEnabled
            .distinctUntilChanged()
            .onEach { enabled ->
                if (enabled) batteryMonitor.start(appScope) else batteryMonitor.stop()
            }
            .launchIn(appScope)
    }

    companion object {
        const val TAG = "Chiflido"
    }
}
