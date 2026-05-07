package com.alkapa.chiflido

import android.app.Application
import com.alkapa.chiflido.core.AlertEngine
import com.alkapa.chiflido.data.AppDatabase
import com.alkapa.chiflido.data.AppSettings

/**
 * Application principal. Expone como singletons la base de datos Room, el
 * almacén de settings y el AlertEngine. El monitor de batería se enchufa
 * en el paso 12.
 */
class ChiflidoApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val settings: AppSettings by lazy { AppSettings(this) }
    val alertEngine: AlertEngine by lazy { AlertEngine(this) }

    override fun onCreate() {
        super.onCreate()
        // Inicializaciones se agregan conforme avanza la implementación.
    }

    companion object {
        const val TAG = "Chiflido"
    }
}
