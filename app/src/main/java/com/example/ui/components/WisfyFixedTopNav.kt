package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WarmSurfaceCard

@Composable
fun WisfyFixedTopNav(
    isDaemonActive: Boolean,
    onSimulateClick: () -> Unit,
    onDocsClick: () -> Unit,
    onAddEndpointClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for live daemon status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        WarmSurfaceCard,
                        WarmSurfaceCard.copy(alpha = 0.98f)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        // Specular top highlight line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, PureWhite.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Sleek Brand Logo + Title + TLS 1.3 SECURE Status Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onDocsClick)
                    .padding(vertical = 2.dp)
            ) {
                WisfyBrandShape(
                    modifier = Modifier.size(34.dp),
                    contentDescription = "Wisfy Logo"
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "WISFY",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        // Subtle Green Status Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (isDaemonActive) EmeraldLight else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            PureWhite.copy(alpha = 0.85f),
                                            if (isDaemonActive) EmeraldGreen.copy(alpha = 0.4f) else WarmBorder
                                        )
                                    ),
                                    RoundedCornerShape(9999.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .scale(if (isDaemonActive) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(if (isDaemonActive) EmeraldGreen else Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDaemonActive) "TLS 1.3 SECURE" else "STANDBY",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isDaemonActive) EmeraldDark else TextSecondary
                            )
                        }
                    }
                    Text(
                        text = "SMS GATEWAY",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen,
                        letterSpacing = 0.7.sp
                    )
                }
            }

            // Right: Paired Auxiliary Controls (Simulate + API Docs) + Prominent Green '+' FAB
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Paired minimalist capsule for secondary tools (Play & Docs)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(PureWhite.copy(alpha = 0.9f), WarmBorder)
                            ),
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    // Quick Simulate SMS
                    IconButton(
                        onClick = onSimulateClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("top_nav_simulate_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Simulate SMS",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Vertical hairline separator
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(16.dp)
                            .background(WarmBorder.copy(alpha = 0.8f))
                    )

                    // API Docs & Multi-stack Code
                    IconButton(
                        onClick = onDocsClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("top_nav_docs_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "API Docs",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Prominent Glossy Circular Green '+' Action Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = CircleShape,
                            spotColor = EmeraldGreen.copy(alpha = 0.45f),
                            ambientColor = Color(0x12000000)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF22C55E),
                                    Color(0xFF16A34A),
                                    Color(0xFF15803D)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    PureWhite.copy(alpha = 0.75f),
                                    Color(0x33FFFFFF),
                                    Color(0x05FFFFFF)
                                )
                            ),
                            CircleShape
                        )
                        .clickable(onClick = onAddEndpointClick)
                        .testTag("top_nav_add_endpoint_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Connect Webhook",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Apple Hairline bottom separator with subtle gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(WarmBorder, WarmBorder.copy(alpha = 0.4f))
                    )
                )
        )
    }
}

