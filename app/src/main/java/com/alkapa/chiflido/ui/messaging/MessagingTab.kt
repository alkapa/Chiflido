package com.alkapa.chiflido.ui.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkapa.chiflido.R
import com.alkapa.chiflido.data.WatchedContact

/**
 * Tab de mensajería: lista los contactos vigilados y permite editarlos.
 * En el paso 6 sólo muestra empty state; el paso 7 conecta el contact picker
 * y la edición.
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = contacts, key = { it.id }) { contact ->
                ContactRowPlaceholder(contact)
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
            text = androidx.compose.ui.res.stringResource(id = R.string.messaging_empty),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Placeholder hasta el paso 7. Se reemplaza por `ContactItem` editable.
 */
@Composable
private fun ContactRowPlaceholder(contact: WatchedContact) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
        Text(text = contact.packageName, style = MaterialTheme.typography.bodyMedium)
    }
}
