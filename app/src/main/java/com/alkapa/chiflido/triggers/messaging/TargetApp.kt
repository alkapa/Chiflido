package com.alkapa.chiflido.triggers.messaging

/**
 * App de mensajería que el usuario elige al agregar un contacto vigilado.
 *
 * El package name canónico de SMS varía entre OEMs (Google Messages, Samsung
 * Messages, etc.); por simplicidad MVP usamos Google Messages. Para soportar
 * otros, agregar entradas al enum o aceptar una lista por valor.
 */
enum class TargetApp(val packageName: String, val displayName: String) {
    WHATSAPP("com.whatsapp", "WhatsApp"),
    SMS("com.google.android.apps.messaging", "SMS"),
    TELEGRAM("org.telegram.messenger", "Telegram");

    companion object {
        val allKnownPackages: Set<String> = values().map { it.packageName }.toSet()

        fun fromPackage(pkg: String): TargetApp? = values().firstOrNull { it.packageName == pkg }
    }
}
