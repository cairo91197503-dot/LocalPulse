package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F52BA),      // Sapphire Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEafe),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF10B981),    // Emerald Green
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),     // Amber Gold
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),   // Slate light
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),      // Lighter Blue
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEafe),
    secondary = Color(0xFF34D399),    // Lighter Green
    onSecondary = Color(0xFF064E3B),
    tertiary = Color(0xFFFBBF24),     // Lighter Gold
    onTertiary = Color(0xFF78350F),
    background = Color(0xFF0F172A),   // Dark Slate
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

@Composable
fun LocalPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
