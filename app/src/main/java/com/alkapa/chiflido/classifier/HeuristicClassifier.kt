package com.alkapa.chiflido.classifier

import java.text.Normalizer

/**
 * Heurística sencilla de importancia. Las reglas se evalúan en orden
 * descendente; la primera que matchea gana, así "ayuda urgente" devuelve 5
 * aunque también cumpla otras condiciones.
 *
 * Reglas:
 *   5 — contiene cualquiera de las palabras urgentes (acentos normalizados).
 *   4 — length > 200.
 *   3 — contiene '?'.
 *   2 — length < 20.
 *   1 — caso default.
 */
class HeuristicClassifier(
    private val urgentWords: Set<String> = DEFAULT_URGENT_WORDS,
) : MessageClassifier {

    override fun classify(text: String): Int {
        val normalized = text.normalizeForMatch()
        if (urgentWords.any { it.normalizeForMatch() in normalized }) return 5
        if (text.length > 200) return 4
        if (text.contains('?')) return 3
        if (text.length < 20) return 2
        return 1
    }

    companion object {
        // Las palabras quedan normalizadas en runtime para tolerar mayúsculas
        // y acentos.
        val DEFAULT_URGENT_WORDS: Set<String> = setOf(
            "urgente",
            "emergencia",
            "ayuda",
            "auxilio",
            "hospital",
            "accidente",
            "ahora",
            "ya",
        )
    }
}

/**
 * Lowercase + descomposición Unicode + remoción de marcas para que
 * "Ayúdame" matchee "ayuda" sin importar acentos o caja.
 */
private fun String.normalizeForMatch(): String {
    val lower = lowercase()
    val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
    return decomposed.replace(Regex("\\p{Mn}+"), "")
}
