package com.alkapa.chiflido.core

import kotlinx.coroutines.CoroutineScope

/**
 * Productor de eventos. NotifListener es un Trigger implícito (lo arranca el
 * sistema); BatteryMonitor implementa esta interfaz para que ChiflidoApp lo
 * arranque/pare con el switch global.
 */
interface Trigger {
    fun start(scope: CoroutineScope)
    fun stop()
}
