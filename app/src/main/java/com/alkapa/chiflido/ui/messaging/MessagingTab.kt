package com.alkapa.chiflido.ui.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkapa.chiflido.R

/**
 * Tab de mensajería: lista los contactos vigilados y permite editarlos
 * inline. El FAB de la pantalla principal lanza el contact picker.
 */
@Composable
fun MessagingTab(
    viewModel: MessagingViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()

    if (contacts.isEmpty()) {
        EmptyContacts(modifier = modifier.padding(contentPadding))
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items = contacts, key = { it.id }) { contact ->
                ContactItem(
                    contact = contact,
                    onUpdate = viewModel::updateContact,
                    onDelete = viewModel::deleteContact,
                )
            }
        }
    }
}

@Composable
private fun EmptyContacts(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.messaging_empty),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
