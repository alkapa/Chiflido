package com.alkapa.chiflido.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = ChiflidoOrange,
    onPrimary = ChiflidoSurface,
    primaryContainer = Color(0xFFFFD8C9),
    secondary = ChiflidoTeal,
    onSecondary = ChiflidoSurface,
    tertiary = ChiflidoAmber,
    error = ChiflidoError,
    background = ChiflidoBackground,
    surface = ChiflidoSurface,
    outline = ChiflidoOutline,
)

private val DarkColors = darkColorScheme(
    primary = ChiflidoOrangeLightOnDark,
    onPrimary = Color(0xFF541700),
    primaryContainer = ChiflidoOrangeDark,
    secondary = ChiflidoTealLightOnDark,
    tertiary = ChiflidoAmberOnDark,
    error = ChiflidoErrorOnDark,
    background = ChiflidoBackgroundDark,
    surface = ChiflidoSurfaceDark,
    outline = ChiflidoOutlineDark,
)

/**
 * Tema raíz de la app. Activa dynamic color en Android 12+; en versiones
 * anteriores cae a la paleta estática definida en Color.kt.
 */
@Composable
fun ChiflidoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChiflidoTypography,
        content = content,
    )
}
