package com.tenshin.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════
//  Esquema Normal (Warframe Clásico)
// ══════════════════════════════════════════
private val TenshinDarkColorScheme = darkColorScheme(
    primary          = ColorAccent,
    background       = ColorBg,
    surface          = ColorSurface,
    onPrimary        = ColorBg,
    onBackground     = ColorText,
    onSurface        = ColorText,
    outline          = ColorBorder,
)

// ══════════════════════════════════════════
//  Esquema Hacked (Höllvania 1999)
// ══════════════════════════════════════════
private val HolvaniaColorScheme = darkColorScheme(
    primary          = ColorHackerGreen,
    background       = ColorHackerBg,
    surface          = ColorHackerSurface,
    onPrimary        = ColorHackerBg,
    onBackground     = ColorHackerGreen,
    onSurface        = ColorHackerGreen,
    outline          = ColorHackerGreen.copy(alpha = 0.5f),
)

// Composable local para rastrear el estado del hack
val LocalIsHackedMode = staticCompositionLocalOf { mutableStateOf(false) }

@Composable
fun TenshinTheme(
    isHacked: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isHacked) HolvaniaColorScheme else TenshinDarkColorScheme
    
    // Sobrescribir colores globales para componentes que no usan el esquema de Material3 directamente
    CompositionLocalProvider(LocalIsHackedMode provides remember { mutableStateOf(isHacked) }) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = TenshinTypography,
            content     = content,
        )
    }
}
