package com.alkapa.chiflido.triggers.messaging

import com.alkapa.chiflido.classifier.MessageClassifier
import com.alkapa.chiflido.core.AlertRule
import com.alkapa.chiflido.core.AlertSpec
import com.alkapa.chiflido.core.TriggerEvent
import com.alkapa.chiflido.core.TriggerSource
import com.alkapa.chiflido.data.WatchedContact

/**
 * Decide si un evento de mensajería amerita disparar alerta:
 *
 *  1. Encuentra el primer WatchedContact habilitado cuyo packageName matchea
 *     y cuyo `name` está contenido en el título (case-insensitive).
 *  2. Si la notif es de grupo y el contacto tiene `ignoreGroups`, descarta.
 *  3. Clasifica el texto; si el score < umbral, descarta.
 *  4. Devuelve un AlertSpec con título "Chiflido: {nombre}".
 *
 * `loadEnabledContacts` se inyecta para que el listener pueda pasar el
 * snapshot del DAO sin que esta clase necesite Room directamente — facilita
 * los tests unitarios.
 */
class MessageRule(
    private val classifier: MessageClassifier,
    private val loadEnabledContacts: suspend () -> List<WatchedContact>,
) : AlertRule<TriggerEvent.Message> {

    override suspend fun evaluate(event: TriggerEvent.Message): AlertSpec? {
        val contacts = loadEnabledContacts()
        val match = contacts.firstOrNull { contact ->
            contact.packageName == event.packageName &&
                event.title.contains(contact.name, ignoreCase = true)
        } ?: return null

        if (event.isGroup && match.ignoreGroups) return null

        val score = classifier.classify(event.text)
        if (score < match.minImportance) return null

        return AlertSpec(
            title = "Chiflido: ${match.name}",
            body = event.text,
            severity = score,
            source = TriggerSource.MESSAGING,
        )
    }
}
