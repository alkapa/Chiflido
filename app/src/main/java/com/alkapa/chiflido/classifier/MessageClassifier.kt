package com.alkapa.chiflido.classifier

/**
 * Clasificador de importancia de un mensaje (1..5). El pipeline trata el
 * resultado como un score; cualquier implementación —heurística, ML, Gemini
 * remoto— se enchufa cumpliendo esta interfaz.
 */
fun interface MessageClassifier {
    fun classify(text: String): Int
}
