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
    primary = Color(0xFF0F172A),
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFECFDF5),
    onPrimaryContainer = Color(0xFF047857),
    secondary = Color(0xFF059669),
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF047857),
    tertiary = AmberWarning,
    onTertiary = PureWhite,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberWarning,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F5F9),
    surfaceContainerLowest = PureWhite,
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1),
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorContainer
)

// Deep Charcoal & Obsidian Dark Color Scheme
private val WisfyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = PureBlack,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFF34D399),
    secondary = Color(0xFF34D399),
    onSecondary = PureBlack,
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFF34D399),
    tertiary = AmberWarning,
    onTertiary = PureBlack,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = AmberWarning,
    background = Color(0xFF0C1017),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF121822),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainer = Color(0xFF161F2C),
    surfaceContainerLow = Color(0xFF121822),
    surfaceContainerLowest = Color(0xFF0C1017),
    outline = Color(0xFF233042),
    outlineVariant = Color(0xFF334358),
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
