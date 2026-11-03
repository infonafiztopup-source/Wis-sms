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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppleFeatureRow
import com.example.ui.components.BrandTag
import com.example.ui.components.ButtonVariant
import com.example.ui.components.WisfyBrandShape
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.PureWhite

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
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Linear Category Pill
            BrandTag(text = "WISFY GATEWAY • LOCAL-FIRST")

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Title with Inter typography
            Text(
                text = "Welcome to Wisfy",
                fontFamily = InterFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.8).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "The ultra-fast, local-first SMS-to-Webhook relay for payment gateways, 2FA verifications, and custom backend automation.",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Clean Emblem Container (Borderless Logo)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                // Subtle ambient glow ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .border(1.dp, colors.borderSubtle, CircleShape)
                )

                // Clean Borderless Hero Logo Display
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WisfyBrandShape(
                        contentDescription = "Official Wisfy Brand Emblem",
                        modifier = Modifier.size(125.dp)
                    )
                }

                // Linear Floating Pill at Bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(colors.surfaceElevated)
                        .border(
                            1.dp,
                            colors.borderSubtle,
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Direct Device-to-Server",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Feature Highlights Card
            WisfyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppleFeatureRow(
                        icon = Icons.Default.CloudOff,
                        iconColor = EmeraldGreen,
                        iconBgColor = colors.statusSuccessBg,
                        title = "Zero Cloud Relay",
                        description = "Direct edge HTTP/HTTPS delivery to your endpoints. Your financial SMS never touches 3rd-party servers."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.borderSubtle)
                    )

                    AppleFeatureRow(
                        icon = Icons.Default.PhoneAndroid,
                        iconColor = Color(0xFF2563EB),
                        iconBgColor = if (colors.isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                        title = "Dual-SIM Routing & Whitelists",
                        description = "Selectively route SIM 1, SIM 2, or both to dedicated webhooks with strict sender regex filtering."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.borderSubtle)
                    )

                    AppleFeatureRow(
                        icon = Icons.Default.Fingerprint,
                        iconColor = Color(0xFF8B5CF6),
                        iconBgColor = if (colors.isDark) Color(0xFF1E293B) else Color(0xFFF5F3FF),
                        title = "2-Layer Deduplication",
                        description = "SHA-256 payload and transaction ID idempotency prevents duplicate chargebacks or replay attacks."
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom CTA & Attribution
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            WisfyButton(
                text = "Continue to Setup",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onContinue,
                variant = ButtonVariant.PRIMARY,
                height = 48.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_continue_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "By continuing you agree to local processing & device safety rules",
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Developed by Shahria Nafiz Sajjad",
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = EmeraldDark
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
