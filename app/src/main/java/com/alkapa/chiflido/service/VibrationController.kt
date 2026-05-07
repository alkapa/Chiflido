package com.alkapa.chiflido.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Wrapper sobre Vibrator/VibratorManager con patrones por severidad.
 * `start` es reentrante: si ya está vibrando con la misma severidad, no
 * hace nada; si cambia, reinicia con el patrón nuevo.
 */
class VibrationController(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        mgr.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private var currentSeverity: Int = -1

    fun start(severity: Int) {
        val sev = severity.coerceIn(1, 5)
        if (sev == currentSeverity) return
        vibrator.cancel()
        val pattern = patternFor(sev)
        // repeat = 0 => loop completo desde el índice 0
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        currentSeverity = sev
    }

    fun stop() {
        vibrator.cancel()
        currentSeverity = -1
    }

    private fun patternFor(severity: Int): LongArray = when (severity) {
        5 -> longArrayOf(0, 800, 200)
        4 -> longArrayOf(0, 600, 400)
        3 -> longArrayOf(0, 500, 500)
        2 -> longArrayOf(0, 400, 800)
        else -> longArrayOf(0, 300, 1200)
    }
}
