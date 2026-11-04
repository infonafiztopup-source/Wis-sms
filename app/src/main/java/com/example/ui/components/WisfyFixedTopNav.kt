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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
    userName: String = "Nafiz",
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceCard)
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .clickable(onClick = onDocsClick),
                    contentAlignment = Alignment.Center
                ) {
                    WisfyBrandShape(
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Wisfy Flame"
                    )
                }

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

                // Docs / Quick Info Icon Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
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
