package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1A1500),
    primaryContainer = Color(0xFF423405),
    onPrimaryContainer = GoldLight,
    secondary = AmberAccent,
    onSecondary = Color(0xFF1A1100),
    secondaryContainer = Color(0xFF422C00),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = BaridiMobGreenLight,
    onTertiary = Color.White,
    background = MidnightNavy,
    onBackground = Color(0xFFF1F5F9),
    surface = NavySurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = GoldDark
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = AmberAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFFBEB),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = BaridiMobGreen,
    onTertiary = Color.White,
    background = ParchmentLight,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFFD97706)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
