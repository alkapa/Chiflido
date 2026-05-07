package com.alkapa.chiflido

import android.app.Application
import com.alkapa.chiflido.data.AppDatabase
import com.alkapa.chiflido.data.AppSettings

/**
 * Application principal. Expone como singletons la base de datos Room y el
 * almacén de settings. El AlertEngine y el monitor de batería se enchufan
 * en pasos posteriores.
 */
class ChiflidoApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val settings: AppSettings by lazy { AppSettings(this) }

    override fun onCreate() {
        super.onCreate()
        // Inicializaciones se agregan conforme avanza la implementación.
    }

    companion object {
        const val TAG = "Chiflido"
    }
}
