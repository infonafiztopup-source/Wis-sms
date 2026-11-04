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
val ErrorRed = Color(0xFFE5484D)
val ErrorContainer = Color(0xFFFEE2E2)
val AmberWarning = Color(0xFFF08A24)
val AmberContainer = Color(0xFFFFFBEB)
val InfoPurple = Color(0xFF4A6CF7)
val InfoContainer = Color(0xFFEFF2FE)

// Data structure holding all dynamic semantic colors for "Graphite Wallet" design
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
    val statusDuplicate: Color,
    // Graphite Wallet Specific Tokens
    val heroGradientTop: Color = Color(0xFF2C2C2E),
    val heroGradientBottom: Color = Color(0xFF111113),
    val heroTexture: Color = Color(0x0FFFFFFF), // white @ 6%
    val heroPillBg: Color = Color(0x14FFFFFF), // white @ 8%
    val metalTop: Color = Color(0xFFFFFFFF),
    val metalBottom: Color = Color(0xFFBDBDBD),
    val ctaGradientTop: Color = Color(0xFF2A2A2C),
    val ctaGradientBottom: Color = Color(0xFF0E0E10),
    val navPill: Color = Color(0xFF000000),
    val navActiveCircle: Color = Color(0xFFFFFFFF),
    val navActiveIcon: Color = Color(0xFF000000),
    val navInactiveIcon: Color = Color(0xFF8E8E93),
    val chartBar: Color = Color(0xFF333333),
    val chartGrid: Color = Color(0x0F000000)
) {
    val canvasBackground: Color get() = background
    val surfaceLow: Color get() = surfaceHighlight
    val borderSubtle: Color get() = border
    val statusSuccess: Color get() = statusDelivered
    val statusSuccessBg: Color get() = emeraldLight
    val statusError: Color get() = statusFailed
    val statusErrorBg: Color get() = ErrorContainer
}

// "Graphite Wallet" Light Palette: Cool light-gray canvas, layered white cards, dark hero & cta
val LightWisfyColors = WisfyColors(
    isDark = false,
    background = Color(0xFFE8E8E8), // Cool light-gray canvas
    surface = Color(0xFFF7F7F7),    // Outer card
    surfaceCard = Color(0xFFFFFFFF), // Inner card
    surfaceHighlight = Color(0xFFEFEFEF),
    surfaceElevated = Color(0xFFFFFFFF),
    border = Color(0xFFE2E2E4),
    borderVariant = Color(0xFFD2D2D6),
    borderActive = Color(0xFF22B80F),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF6B6B70),
    textMuted = Color(0xFF8E8E93),
    emerald = Color(0xFF22B80F),
    emeraldDark = Color(0xFF1A9A0C),
    emeraldLight = Color(0xFFD6F2D0),
    emeraldBadge = Color(0xFFC4EBBC),
    buttonPrimary = Color(0xFF1A1A1C),
    buttonOnPrimary = Color(0xFFFFFFFF),
    statusDelivered = Color(0xFF22B80F),
    statusPending = Color(0xFFF08A24),
    statusFailed = Color(0xFFE5484D),
    statusDuplicate = Color(0xFF4A6CF7),
    heroGradientTop = Color(0xFF2C2C2E),
    heroGradientBottom = Color(0xFF111113),
    heroTexture = Color(0x0FFFFFFF),
    heroPillBg = Color(0x14FFFFFF),
    metalTop = Color(0xFFFFFFFF),
    metalBottom = Color(0xFFBDBDBD),
    ctaGradientTop = Color(0xFF2A2A2C),
    ctaGradientBottom = Color(0xFF0E0E10),
    navPill = Color(0xFF000000),
    navActiveCircle = Color(0xFFFFFFFF),
    navActiveIcon = Color(0xFF000000),
    navInactiveIcon = Color(0xFF8E8E93),
    chartBar = Color(0xFF333333),
    chartGrid = Color(0x0F000000)
)

// "Graphite Wallet" Dark Palette: Intentional deep obsidian canvas, layered dark cards
val DarkWisfyColors = WisfyColors(
    isDark = true,
    background = Color(0xFF0B0B0C),
    surface = Color(0xFF151517),    // Outer card
    surfaceCard = Color(0xFF1D1D20), // Inner card
    surfaceHighlight = Color(0xFF26262A),
    surfaceElevated = Color(0xFF202024),
    border = Color(0xFF2A2A2E),
    borderVariant = Color(0xFF3A3A40),
    borderActive = Color(0xFF3DDC5A),
    textPrimary = Color(0xFFF5F5F7),
    textSecondary = Color(0xFFA1A1A8),
    textMuted = Color(0xFF6C6C74),
    emerald = Color(0xFF3DDC5A),
    emeraldDark = Color(0xFF6BE882),
    emeraldLight = Color(0x243DDC5A), // 14%
    emeraldBadge = Color(0x383DDC5A), // 22%
    buttonPrimary = Color(0xFFF5F5F7),
    buttonOnPrimary = Color(0xFF0B0B0C),
    statusDelivered = Color(0xFF3DDC5A),
    statusPending = Color(0xFFFFA94D),
    statusFailed = Color(0xFFFF6B70),
    statusDuplicate = Color(0xFF7C93FF),
    heroGradientTop = Color(0xFF2C2C2E),
    heroGradientBottom = Color(0xFF111113),
    heroTexture = Color(0x14FFFFFF),
    heroPillBg = Color(0x1AFFFFFF),
    metalTop = Color(0xFFF2F2F4),
    metalBottom = Color(0xFFA8A8AE),
    ctaGradientTop = Color(0xFFFFFFFF),
    ctaGradientBottom = Color(0xFFD9D9DE),
    navPill = Color(0xFF000000),
    navActiveCircle = Color(0xFFFFFFFF),
    navActiveIcon = Color(0xFF000000),
    navInactiveIcon = Color(0xFF8E8E93),
    chartBar = Color(0xD9D4D4D8),
    chartGrid = Color(0x1AFFFFFF)
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

    val heroGradientTop by animateColorAsState(target.heroGradientTop, animationSpec, label = "heroGradientTop")
    val heroGradientBottom by animateColorAsState(target.heroGradientBottom, animationSpec, label = "heroGradientBottom")
    val heroTexture by animateColorAsState(target.heroTexture, animationSpec, label = "heroTexture")
    val heroPillBg by animateColorAsState(target.heroPillBg, animationSpec, label = "heroPillBg")
    val metalTop by animateColorAsState(target.metalTop, animationSpec, label = "metalTop")
    val metalBottom by animateColorAsState(target.metalBottom, animationSpec, label = "metalBottom")
    val ctaGradientTop by animateColorAsState(target.ctaGradientTop, animationSpec, label = "ctaGradientTop")
    val ctaGradientBottom by animateColorAsState(target.ctaGradientBottom, animationSpec, label = "ctaGradientBottom")
    val navPill by animateColorAsState(target.navPill, animationSpec, label = "navPill")
    val navActiveCircle by animateColorAsState(target.navActiveCircle, animationSpec, label = "navActiveCircle")
    val navActiveIcon by animateColorAsState(target.navActiveIcon, animationSpec, label = "navActiveIcon")
    val navInactiveIcon by animateColorAsState(target.navInactiveIcon, animationSpec, label = "navInactiveIcon")
    val chartBar by animateColorAsState(target.chartBar, animationSpec, label = "chartBar")
    val chartGrid by animateColorAsState(target.chartGrid, animationSpec, label = "chartGrid")

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
        statusDuplicate = statusDuplicate,
        heroGradientTop = heroGradientTop,
        heroGradientBottom = heroGradientBottom,
        heroTexture = heroTexture,
        heroPillBg = heroPillBg,
        metalTop = metalTop,
        metalBottom = metalBottom,
        ctaGradientTop = ctaGradientTop,
        ctaGradientBottom = ctaGradientBottom,
        navPill = navPill,
        navActiveCircle = navActiveCircle,
        navActiveIcon = navActiveIcon,
        navInactiveIcon = navInactiveIcon,
        chartBar = chartBar,
        chartGrid = chartGrid
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
