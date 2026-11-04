package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors

/**
 * Clean Fintech Wallet Header (Reference: "Welcome, Salung" with right action icons)
 * Sits seamlessly on the cool gray canvas with no heavy toolbars.
 */
@Composable
fun WisfyFixedTopNav(
    isDaemonActive: Boolean,
    onSimulateClick: () -> Unit = {},
    onDocsClick: () -> Unit = {},
    onAddEndpointClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    userName: String = android.os.Build.MODEL.ifBlank { "Merchant" },
    modifier: Modifier = Modifier
) {
    val colors = LocalWisfyColors.current

    // Pulsing live activity beacon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: "Welcome, Name" Title + Small Flame Brand Emblem
            Row(verticalAlignment = Alignment.CenterVertically) {
                WisfyLogoBadge(
                    size = 38.dp,
                    logoSize = 22.dp,
                    cornerRadius = 12.dp,
                    modifier = Modifier.clickable(onClick = onDocsClick)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Welcome, $userName",
                    fontFamily = InterFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.4).sp
                )
            }

            // Right: Status Badge & Settings Icon Button (Reference: Kebab/Gear icons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Service Activity Beacon
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(colors.surfaceCard)
                        .border(1.dp, colors.border, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .scale(if (isDaemonActive) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(if (isDaemonActive) colors.emerald else colors.statusPending)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isDaemonActive) "LIVE" else "PAUSED",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDaemonActive) colors.emeraldDark else colors.textMuted
                    )
                }

                val haptic = LocalHapticFeedback.current
                val isDark = colors.isDark

                // Smooth 360-degree rotation animation for theme icon
                val rotationAngle by animateFloatAsState(
                    targetValue = if (isDark) 360f else 0f,
                    animationSpec = spring(
                        dampingRatio = 0.65f,
                        stiffness = 360f
                    ),
                    label = "themeRotate"
                )

                // Expanding celestial radiant aura ripple on theme switch
                val rippleEffect = remember { Animatable(0f) }
                LaunchedEffect(isDark) {
                    rippleEffect.snapTo(0f)
                    rippleEffect.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    )
                }

                // Theme Toggle Container with Radiant Halo & Specular Glass Tile
                Box(contentAlignment = Alignment.Center) {
                    // Expanding Luminous Pulse Ring
                    if (rippleEffect.value in 0.01f..0.99f) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .scale(1f + rippleEffect.value * 0.85f)
                                .clip(CircleShape)
                                .background(
                                    if (isDark) Color(0xFFFACC15).copy(alpha = (1f - rippleEffect.value) * 0.42f)
                                    else Color(0xFF60A5FA).copy(alpha = (1f - rippleEffect.value) * 0.38f)
                                )
                        )
                    }

                    // Interactive Theme Toggle Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                ambientColor = Color(0x24000000),
                                spotColor = Color(0x38000000)
                            )
                            .clip(CircleShape)
                            .background(colors.surfaceCard)
                            .border(
                                1.dp,
                                if (isDark) Color(0x33FACC15) else colors.border,
                                CircleShape
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleTheme()
                            }
                            .testTag("top_nav_theme_toggle_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.rotate(rotationAngle),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = isDark,
                                transitionSpec = {
                                    (fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                                            scaleIn(initialScale = 0.45f, animationSpec = spring(0.68f, 400f)))
                                        .togetherWith(
                                            fadeOut(tween(150, easing = FastOutSlowInEasing)) +
                                                    scaleOut(targetScale = 0.45f)
                                        )
                                },
                                label = "themeIconContent"
                            ) { dark ->
                                if (dark) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = "Switch to Light Theme",
                                        tint = Color(0xFFFACC15),
                                        modifier = Modifier.size(19.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = "Switch to Dark Theme",
                                        tint = Color(0xFF4B5563),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Docs / Quick Info Icon Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = CircleShape,
                            ambientColor = Color(0x1F000000),
                            spotColor = Color(0x2E000000)
                        )
                        .clip(CircleShape)
                        .background(colors.surfaceCard)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable(onClick = onDocsClick)
                        .testTag("top_nav_docs_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "API Docs",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
