package com.alkapa.chiflido.ui.battery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alkapa.chiflido.ChiflidoApp
import com.alkapa.chiflido.data.BatterySettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BatteryViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = (app as ChiflidoApp).database.batteryDao()

    val settings: StateFlow<BatterySettings> = dao.observe().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BatterySettings(),
    )

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dao.upsert(settings.value.copy(enabled = enabled))
        }
    }

    fun setThreshold(threshold: Int) {
        viewModelScope.launch {
            dao.upsert(settings.value.copy(thresholdPct = threshold.coerceIn(5, 50)))
        }
    }
}
