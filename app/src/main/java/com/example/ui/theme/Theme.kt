package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroColorScheme = darkColorScheme(
    primary = RetroGold,
    onPrimary = RetroDarkBg,
    primaryContainer = RetroGoldDark,
    onPrimaryContainer = RetroTextPrimary,
    secondary = RetroManaBlue,
    onSecondary = RetroDarkBg,
    secondaryContainer = RetroSurfaceVariant,
    onSecondaryContainer = RetroManaBlue,
    tertiary = RetroRuby,
    onTertiary = RetroTextPrimary,
    background = RetroDarkBg,
    onBackground = RetroTextPrimary,
    surface = RetroSurface,
    onSurface = RetroTextPrimary,
    surfaceVariant = RetroSurfaceVariant,
    onSurfaceVariant = RetroTextSecondary,
    outline = RetroCardBorder,
    error = RetroCrimson,
    onError = RetroTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RetroColorScheme,
        typography = Typography,
        content = content
    )
}
