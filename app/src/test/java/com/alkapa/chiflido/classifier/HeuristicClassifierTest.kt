package com.alkapa.chiflido.classifier

import org.junit.Assert.assertEquals
import org.junit.Test

class HeuristicClassifierTest {

    private val sut = HeuristicClassifier()

    @Test fun `palabra urgente devuelve 5`() {
        assertEquals(5, sut.classify("ayuda por favor"))
        assertEquals(5, sut.classify("URGENTE!"))
    }

    @Test fun `tolera acentos y mayusculas en palabra urgente`() {
        // "ayúdame" debe matchear "ayuda" tras normalización.
        assertEquals(5, sut.classify("Ayúdame"))
        assertEquals(5, sut.classify("EMERGENCIA"))
    }

    @Test fun `palabra urgente gana sobre otras reglas`() {
        // Texto largo > 200 + signo de interrogación + palabra urgente => 5.
        val largo = "ayuda " + "x".repeat(250) + "?"
        assertEquals(5, sut.classify(largo))
    }

    @Test fun `texto largo sin urgente devuelve 4`() {
        val largo = "x".repeat(250)
        assertEquals(4, sut.classify(largo))
    }

    @Test fun `pregunta corta devuelve 3`() {
        assertEquals(3, sut.classify("¿estas?"))
    }

    @Test fun `pregunta gana sobre length corto`() {
        // length < 20 sería 2, pero contiene '?' => 3 (la regla 3 va antes).
        assertEquals(3, sut.classify("ok?"))
    }

    @Test fun `texto corto sin nada especial devuelve 2`() {
        assertEquals(2, sut.classify("ok"))
    }

    @Test fun `texto medio sin nada especial devuelve 1`() {
        assertEquals(1, sut.classify("hola, te paso un mensaje normal sin nada raro"))
    }
}
