package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Https
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WisfyBrandShape
import com.example.ui.components.WisfyCtaButton
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfyShapes
import com.example.ui.theme.WisfySpacing

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = LocalWisfyColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = WisfySpacing.screenPadding, vertical = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Logo mark in a white icon tile with soft glow (Flame icon)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = WisfyShapes.OuterCard,
                        ambientColor = Color(0x26000000),
                        spotColor = Color(0x33000000)
                    )
                    .clip(WisfyShapes.OuterCard)
                    .background(colors.surfaceCard)
                    .border(1.dp, colors.border, WisfyShapes.OuterCard),
                contentAlignment = Alignment.Center
            ) {
                WisfyBrandShape(
                    modifier = Modifier.size(44.dp),
                    contentDescription = "Wisfy Flame Logo"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline "Your SMS. Your webhook." (bold 30sp)
            Text(
                text = "Your SMS.\nYour webhook.",
                fontFamily = InterFontFamily,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.8).sp,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Zero cloud intermediary. Forward instant payments and 2FA SMS directly to your server.",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3 feature rows in a layered card (Local-first, Secure HTTPS, OTP-aware)
            WisfyLayeredCard(
                title = "Built for Developers & Merchants"
            ) {
                WelcomeFeatureRow(
                    icon = Icons.Default.CloudOff,
                    iconTint = colors.emerald,
                    title = "100% Local-First Engine",
                    description = "Runs directly on your Android device with zero third-party telemetry."
                )

                Spacer(modifier = Modifier.height(16.dp))

                WelcomeFeatureRow(
                    icon = Icons.Default.Https,
                    iconTint = colors.statusDuplicate,
                    title = "Secure TLS 1.3 Webhooks",
                    description = "Dispatches signed HMAC payloads with automatic retry and deduplication."
                )

                Spacer(modifier = Modifier.height(16.dp))

                WelcomeFeatureRow(
                    icon = Icons.Default.Password,
                    iconTint = colors.statusPending,
                    title = "OTP & Privacy Aware",
                    description = "Built-in filters sanitize card numbers and prevent credential leaks."
                )
            }
        }

        // Bottom CTA Button "Get started"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            WisfyCtaButton(
                text = "Get started",
                onClick = onContinue,
                testTag = "welcome_continue_btn"
            )
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    val colors = LocalWisfyColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        WisfyIconTile(
            icon = icon,
            tint = iconTint,
            containerColor = colors.surfaceHighlight,
            size = 38.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                letterSpacing = (-0.2).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = InterFontFamily,
                fontSize = 12.5.sp,
                color = colors.textSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
