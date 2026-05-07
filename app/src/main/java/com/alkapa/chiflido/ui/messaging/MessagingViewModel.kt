package com.alkapa.chiflido.ui.messaging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alkapa.chiflido.ChiflidoApp
import com.alkapa.chiflido.data.WatchedContact
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessagingViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = (app as ChiflidoApp).database.contactDao()

    val contacts: StateFlow<List<WatchedContact>> = dao.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun addContact(contact: WatchedContact) {
        viewModelScope.launch { dao.insert(contact) }
    }

    fun updateContact(contact: WatchedContact) {
        viewModelScope.launch { dao.update(contact) }
    }

    fun deleteContact(contact: WatchedContact) {
        viewModelScope.launch { dao.delete(contact) }
    }
}
