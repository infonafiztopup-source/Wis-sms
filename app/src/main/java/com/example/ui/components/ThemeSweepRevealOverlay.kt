package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

/**
 * Ultra-Premium Circular Sweep Theme Reveal Overlay.
 *
 * When the theme is toggled, an elegant liquid wave of light or shadow sweeps
 * diagonally across the entire screen from the top-right toggle button origin,
 * with a luminous electric wavefront ring, smoothly unveiling the new theme.
 */
@Composable
fun ThemeSweepRevealOverlay(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var hasInitialized by remember { mutableStateOf(false) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(isDark) {
        if (!hasInitialized) {
            hasInitialized = true
            return@LaunchedEffect
        }
        // Run full-screen circular sweep from the top-right button
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 520,
                easing = CubicBezierEasing(0.2f, 0.0f, 0.1f, 1.0f)
            )
        )
    }

    val progress = animProgress.value
    if (progress in 0.001f..0.999f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Origin at top-right theme toggle button position
            val origin = Offset(size.width - 40.dp.toPx(), 48.dp.toPx())
            // Max radius needed to cover the furthest corner (bottom-left)
            val maxRadius = hypot(origin.x, size.height - origin.y) * 1.15f
            val currentRadius = maxRadius * progress

            val fillColor = if (isDark) Color(0xFF0B0B0C) else Color(0xFFE8E8E8)
            val edgeGlowColor = if (isDark) Color(0xFF3DDC5A) else Color(0xFFFACC15)
            val fadeOutAlpha = if (progress > 0.85f) (1f - progress) / 0.15f else 1f

            // 1. Expanding filled circular disc washing across the screen
            drawCircle(
                color = fillColor.copy(alpha = 0.96f * fadeOutAlpha),
                radius = currentRadius,
                center = origin
            )

            // 2. Soft outer ambient bloom halo along the wave front
            drawCircle(
                color = edgeGlowColor.copy(alpha = 0.28f * fadeOutAlpha),
                radius = currentRadius,
                center = origin,
                style = Stroke(width = 16.dp.toPx())
            )

            // 3. Crisp luminous electric wavefront ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        edgeGlowColor.copy(alpha = 0.85f * fadeOutAlpha),
                        edgeGlowColor.copy(alpha = 0.40f * fadeOutAlpha),
                        Color.Transparent
                    ),
                    center = origin,
                    radius = currentRadius + 6.dp.toPx()
                ),
                radius = currentRadius,
                center = origin,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}
