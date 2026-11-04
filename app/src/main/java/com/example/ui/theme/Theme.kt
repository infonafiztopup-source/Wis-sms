package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// "Graphite Wallet" Light Scheme: Cool light-gray canvas, layered cards, dark hero
private val WisfyLightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A1C),
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFEFEFEF),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = Color(0xFF6B6B70),
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFE2E2E4),
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = Color(0xFF8E8E93),
    onTertiary = PureWhite,
    tertiaryContainer = Color(0xFFEFEFEF),
    onTertiaryContainer = Color(0xFF1A1A1A),
    background = Color(0xFFE8E8E8), // Cool light-gray canvas
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFF7F7F7),    // Outer card
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEFEFEF),
    onSurfaceVariant = Color(0xFF6B6B70),
    surfaceContainer = Color(0xFFFFFFFF), // Inner card
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainerLowest = Color(0xFFE8E8E8),
    outline = Color(0xFFE2E2E4),
    outlineVariant = Color(0xFFD2D2D6),
    error = Color(0xFFE5484D),
    onError = PureWhite,
    errorContainer = Color(0xFFFEE2E2)
)

// "Graphite Wallet" Dark Scheme: Deep charcoal & pure black canvas with layered dark cards
private val WisfyDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF5F5F7),
    onPrimary = Color(0xFF0B0B0C),
    primaryContainer = Color(0xFF26262A),
    onPrimaryContainer = Color(0xFFF5F5F7),
    secondary = Color(0xFFA1A1A8),
    onSecondary = Color(0xFF0B0B0C),
    secondaryContainer = Color(0xFF2A2A2E),
    onSecondaryContainer = Color(0xFFF5F5F7),
    tertiary = Color(0xFF6C6C74),
    onTertiary = PureWhite,
    tertiaryContainer = Color(0xFF26262A),
    onTertiaryContainer = Color(0xFFF5F5F7),
    background = Color(0xFF0B0B0C), // Deep canvas
    onBackground = Color(0xFFF5F5F7),
    surface = Color(0xFF151517),    // Outer card
    onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Color(0xFF26262A),
    onSurfaceVariant = Color(0xFFA1A1A8),
    surfaceContainer = Color(0xFF1D1D20), // Inner card
    surfaceContainerLow = Color(0xFF151517),
    surfaceContainerLowest = Color(0xFF0B0B0C),
    outline = Color(0xFF2A2A2E),
    outlineVariant = Color(0xFF3A3A40),
    error = Color(0xFFFF6B70),
    onError = Color(0xFF0B0B0C),
    errorContainer = Color(0xFF3F1B1D)
)

@Composable
fun WisfyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val targetColors = if (darkTheme) DarkWisfyColors else LightWisfyColors
    val wisfyColors = animateWisfyColorsAsState(targetColors)
    val colorScheme = if (darkTheme) WisfyDarkColorScheme else WisfyLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = wisfyColors.background.toArgb()
                window.navigationBarColor = wisfyColors.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                // When light theme, render dark icons on light status bar. When dark theme, light icons.
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalWisfyColors provides wisfyColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Keep MyApplicationTheme for backward compatibility if referenced
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WisfyTheme(darkTheme = darkTheme, content = content)
}
