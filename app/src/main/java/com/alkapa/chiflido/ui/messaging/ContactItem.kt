package com.alkapa.chiflido.ui.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alkapa.chiflido.R
import com.alkapa.chiflido.data.WatchedContact
import com.alkapa.chiflido.triggers.messaging.TargetApp
import com.alkapa.chiflido.ui.components.SeveritySlider

/**
 * Tarjeta editable por contacto vigilado. Cualquier cambio en los controles
 * dispara `onUpdate(updatedContact)`. La eliminación va por su propio
 * callback para no entorpecer la UX.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(
    contact: WatchedContact,
    onUpdate: (WatchedContact) -> Unit,
    onDelete: (WatchedContact) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = contact.enabled,
                    onCheckedChange = { onUpdate(contact.copy(enabled = it)) },
                )
                IconButton(onClick = { onDelete(contact) }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.contact_delete),
                    )
                }
            }

            // Dropdown de TargetApp.
            var expanded by remember { mutableStateOf(false) }
            val current = TargetApp.fromPackage(contact.packageName)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = current?.displayName ?: contact.packageName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.contact_app)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    TargetApp.values().forEach { app ->
                        DropdownMenuItem(
                            text = { Text(app.displayName) },
                            onClick = {
                                onUpdate(contact.copy(packageName = app.packageName))
                                expanded = false
                            },
                        )
                    }
                }
            }

            // Slider de importancia mínima.
            Column {
                Text(
                    text = stringResource(R.string.contact_min_importance),
                    style = MaterialTheme.typography.bodyMedium,
                )
                SeveritySlider(
                    value = contact.minImportance,
                    onValueChange = { onUpdate(contact.copy(minImportance = it)) },
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.contact_ignore_groups),
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = contact.ignoreGroups,
                    onCheckedChange = { onUpdate(contact.copy(ignoreGroups = it)) },
                )
            }
        }
    }
}
