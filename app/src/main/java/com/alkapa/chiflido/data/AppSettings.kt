package com.alkapa.chiflido.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "chiflido_settings")

/**
 * Settings globales persistidos en DataStore (no necesita el peso de Room).
 *
 * - serviceEnabled: switch global "Servicio activo" en la pantalla principal.
 */
class AppSettings(private val context: Context) {

    private val keyServiceEnabled: Preferences.Key<Boolean> = booleanPreferencesKey("service_enabled")

    val serviceEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[keyServiceEnabled] ?: true
    }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[keyServiceEnabled] = enabled }
    }
}
