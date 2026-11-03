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

// Crisp Modern Light Color Scheme
private val WisfyLightColorScheme = lightColorScheme(
    primary = Color(0xFF19221C),
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFE7F4EC),
    onPrimaryContainer = Color(0xFF086747),
    secondary = Color(0xFF087F5B),
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFD5EBDD),
    onSecondaryContainer = Color(0xFF086747),
    tertiary = AmberWarning,
    onTertiary = PureWhite,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberWarning,
    background = Color(0xFFF6F4EE),
    onBackground = Color(0xFF202720),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF202720),
    surfaceVariant = Color(0xFFF0EEE7),
    onSurfaceVariant = Color(0xFF68736A),
    surfaceContainer = Color(0xFFFFFEFC),
    surfaceContainerLow = Color(0xFFF0EEE7),
    surfaceContainerLowest = Color(0xFFFFFEFA),
    outline = Color(0xFFE6E1D7),
    outlineVariant = Color(0xFFD5CFC3),
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorContainer
)

// Deep Charcoal & Obsidian Dark Color Scheme
private val WisfyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = PureBlack,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFF34D399),
    secondary = Color(0xFF6EE7B7),
    onSecondary = PureBlack,
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFF34D399),
    tertiary = AmberWarning,
    onTertiary = PureBlack,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = AmberWarning,
    background = Color(0xFF0B100D),
    onBackground = Color(0xFFF2F5EF),
    surface = Color(0xFF111813),
    onSurface = Color(0xFFF2F5EF),
    surfaceVariant = Color(0xFF1C2820),
    onSurfaceVariant = Color(0xFFA2ADA2),
    surfaceContainer = Color(0xFF151D17),
    surfaceContainerLow = Color(0xFF111813),
    surfaceContainerLowest = Color(0xFF0B100D),
    outline = Color(0xFF29372D),
    outlineVariant = Color(0xFF3A4A3E),
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = Color(0xFF7F1D1D)
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
