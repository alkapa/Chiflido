package com.alkapa.chiflido.ui.battery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Placeholder hasta el paso 11. Mostramos un texto para que la tab no quede
 * vacía durante el desarrollo.
 */
@Composable
fun BatteryTab(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Próximamente: configura el umbral de batería baja.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
