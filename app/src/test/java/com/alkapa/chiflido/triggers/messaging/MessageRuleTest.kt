package com.alkapa.chiflido.triggers.messaging

import com.alkapa.chiflido.classifier.MessageClassifier
import com.alkapa.chiflido.core.AlertId
import com.alkapa.chiflido.core.TriggerEvent
import com.alkapa.chiflido.core.TriggerSource
import com.alkapa.chiflido.data.WatchedContact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MessageRuleTest {

    private fun ev(
        title: String,
        text: String = "hola",
        pkg: String = "com.whatsapp",
        isGroup: Boolean = false,
    ) = TriggerEvent.Message(
        alertId = AlertId.forNotification("k"),
        packageName = pkg,
        title = title,
        text = text,
        isGroup = isGroup,
    )

    private fun contact(
        name: String = "Juan",
        pkg: String = "com.whatsapp",
        minImportance: Int = 1,
        ignoreGroups: Boolean = true,
        enabled: Boolean = true,
    ) = WatchedContact(
        id = 1,
        name = name,
        packageName = pkg,
        minImportance = minImportance,
        ignoreGroups = ignoreGroups,
        enabled = enabled,
    )

    private val fixedClassifier = MessageClassifier { 3 }

    @Test fun `match contains ic con sufijo de WhatsApp`() = runTest {
        val rule = MessageRule(fixedClassifier) { listOf(contact()) }
        val spec = rule.evaluate(ev(title = "Juan (3 mensajes)"))
        assertNotNull(spec)
        assertEquals("Chiflido: Juan", spec!!.title)
        assertEquals(TriggerSource.MESSAGING, spec.source)
    }

    @Test fun `caso case-insensitive`() = runTest {
        val rule = MessageRule(fixedClassifier) { listOf(contact(name = "JUAN")) }
        assertNotNull(rule.evaluate(ev(title = "juan")))
    }

    @Test fun `sin contactos devuelve null`() = runTest {
        val rule = MessageRule(fixedClassifier) { emptyList() }
        assertNull(rule.evaluate(ev(title = "Juan")))
    }

    @Test fun `package distinto no matchea`() = runTest {
        val rule = MessageRule(fixedClassifier) { listOf(contact(pkg = "com.whatsapp")) }
        assertNull(rule.evaluate(ev(title = "Juan", pkg = "org.telegram.messenger")))
    }

    @Test fun `score menor al umbral devuelve null`() = runTest {
        val rule = MessageRule(MessageClassifier { 2 }) {
            listOf(contact(minImportance = 4))
        }
        assertNull(rule.evaluate(ev(title = "Juan")))
    }

    @Test fun `grupo descartado cuando ignoreGroups true`() = runTest {
        val rule = MessageRule(fixedClassifier) { listOf(contact(ignoreGroups = true)) }
        assertNull(rule.evaluate(ev(title = "Juan", isGroup = true)))
    }

    @Test fun `grupo aceptado cuando ignoreGroups false`() = runTest {
        val rule = MessageRule(fixedClassifier) { listOf(contact(ignoreGroups = false)) }
        assertNotNull(rule.evaluate(ev(title = "Juan", isGroup = true)))
    }
}
