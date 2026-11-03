package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Fixed Universal Values
val PureWhite = Color(0xFFFFFFFF)
val PureBlack = Color(0xFF000000)

// Warning, Error & Info Colors
val ErrorRed = Color(0xFFEF4444)
val ErrorContainer = Color(0xFFFEF2F2)
val AmberWarning = Color(0xFFF59E0B)
val AmberContainer = Color(0xFFFFFBEB)
val InfoPurple = Color(0xFF8B5CF6)
val InfoContainer = Color(0xFFF5F3FF)

// Data structure holding all dynamic semantic colors
data class WisfyColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceHighlight: Color,
    val surfaceElevated: Color,
    val border: Color,
    val borderVariant: Color,
    val borderActive: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val emerald: Color,
    val emeraldDark: Color,
    val emeraldLight: Color,
    val emeraldBadge: Color,
    val buttonPrimary: Color,
    val buttonOnPrimary: Color,
    val statusDelivered: Color,
    val statusPending: Color,
    val statusFailed: Color,
    val statusDuplicate: Color
) {
    val canvasBackground: Color get() = background
    val surfaceLow: Color get() = surfaceHighlight
    val borderSubtle: Color get() = border
    val statusSuccess: Color get() = statusDelivered
    val statusSuccessBg: Color get() = emeraldLight
    val statusError: Color get() = statusFailed
    val statusErrorBg: Color get() = ErrorContainer
}

// Linear / Raycast Inspired Light Theme
val LightWisfyColors = WisfyColors(
    isDark = false,
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceHighlight = Color(0xFFF1F5F9),
    surfaceElevated = Color(0xFFFFFFFF),
    border = Color(0xFFE2E8F0),
    borderVariant = Color(0xFFCBD5E1),
    borderActive = Color(0xFF10B981),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    emerald = Color(0xFF059669),
    emeraldDark = Color(0xFF047857),
    emeraldLight = Color(0xFFECFDF5),
    emeraldBadge = Color(0xFFD1FAE5),
    buttonPrimary = Color(0xFF0F172A),
    buttonOnPrimary = Color(0xFFFFFFFF),
    statusDelivered = Color(0xFF059669),
    statusPending = Color(0xFFD97706),
    statusFailed = Color(0xFFDC2626),
    statusDuplicate = Color(0xFF7C3AED)
)

// Linear / Raycast Deep Charcoal & Obsidian Dark Theme
val DarkWisfyColors = WisfyColors(
    isDark = true,
    background = Color(0xFF0C1017),
    surface = Color(0xFF121822),
    surfaceCard = Color(0xFF161F2C),
    surfaceHighlight = Color(0xFF1E293B),
    surfaceElevated = Color(0xFF1B2433),
    border = Color(0xFF233042),
    borderVariant = Color(0xFF334358),
    borderActive = Color(0xFF10B981),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    emerald = Color(0xFF10B981),
    emeraldDark = Color(0xFF34D399),
    emeraldLight = Color(0xFF064E3B).copy(alpha = 0.45f),
    emeraldBadge = Color(0xFF065F46).copy(alpha = 0.55f),
    buttonPrimary = Color(0xFF10B981),
    buttonOnPrimary = Color(0xFF000000),
    statusDelivered = Color(0xFF10B981),
    statusPending = Color(0xFFF59E0B),
    statusFailed = Color(0xFFEF4444),
    statusDuplicate = Color(0xFFA78BFA)
)

@Composable
fun animateWisfyColorsAsState(
    target: WisfyColors,
    animationSpec: AnimationSpec<Color> = tween(durationMillis = 260)
): WisfyColors {
    val background by animateColorAsState(target.background, animationSpec, label = "bg")
    val surface by animateColorAsState(target.surface, animationSpec, label = "surface")
    val surfaceCard by animateColorAsState(target.surfaceCard, animationSpec, label = "card")
    val surfaceHighlight by animateColorAsState(target.surfaceHighlight, animationSpec, label = "highlight")
    val surfaceElevated by animateColorAsState(target.surfaceElevated, animationSpec, label = "elevated")
    val border by animateColorAsState(target.border, animationSpec, label = "border")
    val borderVariant by animateColorAsState(target.borderVariant, animationSpec, label = "borderVariant")
    val borderActive by animateColorAsState(target.borderActive, animationSpec, label = "borderActive")
    val textPrimary by animateColorAsState(target.textPrimary, animationSpec, label = "textPrimary")
    val textSecondary by animateColorAsState(target.textSecondary, animationSpec, label = "textSecondary")
    val textMuted by animateColorAsState(target.textMuted, animationSpec, label = "textMuted")
    val emerald by animateColorAsState(target.emerald, animationSpec, label = "emerald")
    val emeraldDark by animateColorAsState(target.emeraldDark, animationSpec, label = "emeraldDark")
    val emeraldLight by animateColorAsState(target.emeraldLight, animationSpec, label = "emeraldLight")
    val emeraldBadge by animateColorAsState(target.emeraldBadge, animationSpec, label = "emeraldBadge")
    val buttonPrimary by animateColorAsState(target.buttonPrimary, animationSpec, label = "buttonPrimary")
    val buttonOnPrimary by animateColorAsState(target.buttonOnPrimary, animationSpec, label = "buttonOnPrimary")
    val statusDelivered by animateColorAsState(target.statusDelivered, animationSpec, label = "statusDelivered")
    val statusPending by animateColorAsState(target.statusPending, animationSpec, label = "statusPending")
    val statusFailed by animateColorAsState(target.statusFailed, animationSpec, label = "statusFailed")
    val statusDuplicate by animateColorAsState(target.statusDuplicate, animationSpec, label = "statusDuplicate")

    return WisfyColors(
        isDark = target.isDark,
        background = background,
        surface = surface,
        surfaceCard = surfaceCard,
        surfaceHighlight = surfaceHighlight,
        surfaceElevated = surfaceElevated,
        border = border,
        borderVariant = borderVariant,
        borderActive = borderActive,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        emerald = emerald,
        emeraldDark = emeraldDark,
        emeraldLight = emeraldLight,
        emeraldBadge = emeraldBadge,
        buttonPrimary = buttonPrimary,
        buttonOnPrimary = buttonOnPrimary,
        statusDelivered = statusDelivered,
        statusPending = statusPending,
        statusFailed = statusFailed,
        statusDuplicate = statusDuplicate
    )
}

val LocalWisfyColors: ProvidableCompositionLocal<WisfyColors> = staticCompositionLocalOf {
    LightWisfyColors
}

// Reactive Dynamic Color Properties across all Composable Screens & Dialogs
val ObsidianDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.background

val ObsidianSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surface

val ObsidianCard: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surfaceCard

val ObsidianCardHighlight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surfaceHighlight

val ObsidianCardElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surfaceElevated

val ObsidianBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.border

val ObsidianBorderVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.borderVariant

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.textMuted

val ObsidianBlack: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.buttonPrimary

val WarmSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.background

val WarmSurfaceCard: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surfaceCard

val WarmSurfaceLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.surfaceHighlight

val WarmBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.border

val WarmBorderVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.borderVariant

val EmeraldGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.emerald

val EmeraldDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.emeraldDark

val EmeraldLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.emeraldLight

val EmeraldBadge: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWisfyColors.current.emeraldBadge
