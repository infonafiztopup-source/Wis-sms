package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.delay

/**
 * World-Class App Entering Splash Animation
 * Inspired directly by Reference Image 7 with animated curve loader,
 * high-contrast monochrome typography, and smooth dismissal.
 */
@Composable
fun AppEnteringSplash(
    onFinished: () -> Unit
) {
    val isDark = LocalWisfyColors.current.isDark
    val bg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val textMain = if (isDark) Color(0xFFFAFAFA) else Color(0xFF09090B)
    val textSub = if (isDark) Color(0xFFA1A1AA) else Color(0xFF71717A)
    val curveColor = if (isDark) Color(0xFFFAFAFA) else Color(0xFF09090B)

    var isVisible by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        // Keep splash for 1.3s then gracefully dismiss
        delay(1300)
        isDismissing = true
        delay(280)
        onFinished()
    }

    val alphaAnim by animateFloatAsState(
        targetValue = if (isDismissing) 0f else if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "splashAlpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isDismissing) 1.05f else if (isVisible) 1f else 0.94f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "splashScale"
    )

    // Infinite breathing curve animation for the smiling wave (Reference 7)
    val infiniteTransition = rememberInfiniteTransition(label = "enteringWave")
    val curveDip by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "curveDip"
    )

    val dotPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .scale(scaleAnim)
            .alpha(alphaAnim)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isDismissing = true
                    onFinished()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Top Security & Daemon Badge
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(if (isDark) Color(0xFF18181B) else Color(0xFFF4F4F5))
                .border(
                    1.dp,
                    if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7),
                    RoundedCornerShape(9999.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF10B981) else Color(0xFF09090B))
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "WISFY SMS GATEWAY  •  DAEMON ACTIVE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp,
                color = textMain
            )
        }

        // Center Content: Greeting, Smiling Arc Wave (Reference 7), and Loading Status
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Luminous Flame Emblem
            WisfyLogoBadge(
                size = 72.dp,
                logoSize = 40.dp,
                cornerRadius = 22.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Greeting Headline (Section 5.1 Spec)
            Text(
                text = "Welcome back.",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = textMain,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Wisfy is getting ready…",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = textSub
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Canvas Smile Loader with green accent tip
            WisfySmileLoader(
                size = 54.dp,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Loading text with dot pulse
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "loading",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = textSub,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(textSub.copy(alpha = dotPulse))
                )
                Box(
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(textSub.copy(alpha = 1f - dotPulse))
                )
            }
        }

        // Bottom Tap to Skip Hint
        Text(
            text = "Tap to skip",
            fontSize = 11.sp,
            color = textSub.copy(alpha = 0.5f),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
