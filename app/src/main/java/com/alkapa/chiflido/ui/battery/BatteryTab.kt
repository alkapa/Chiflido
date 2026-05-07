package com.alkapa.chiflido.ui.battery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkapa.chiflido.R

@Composable
fun BatteryTab(
    viewModel: BatteryViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.battery_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = settings.enabled,
                onCheckedChange = viewModel::setEnabled,
            )
        }

        Text(
            text = stringResource(R.string.battery_threshold_label, settings.thresholdPct),
            style = MaterialTheme.typography.bodyLarge,
        )

        Slider(
            value = settings.thresholdPct.toFloat(),
            onValueChange = { viewModel.setThreshold(it.toInt()) },
            valueRange = 5f..50f,
            steps = 8,                       // 5,10,15,...,50 → 10 valores → steps=8
        )

        Text(
            text = stringResource(R.string.battery_help),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
