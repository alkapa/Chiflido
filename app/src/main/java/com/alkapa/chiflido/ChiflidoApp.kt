package com.alkapa.chiflido

import android.app.Application

/**
 * Application principal. En pasos posteriores expone aquí la base de datos
 * Room, el AlertEngine y el monitor de batería como singletons.
 */
class ChiflidoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializaciones se agregan conforme avanza la implementación.
    }

    companion object {
        const val TAG = "Chiflido"
    }
}
