package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardHighlight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WhitelistInfoDialog(
    onDismiss: () -> Unit,
    onApplyRecommended: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = ObsidianDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(EmeraldLight)
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sender Whitelist Filter",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Core Explanation
                Text(
                    text = "What is Sender Whitelist?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A cryptographic privacy and spam filter that controls exactly which SMS senders are permitted to trigger your webhook endpoint. Any message from an unlisted sender is automatically ignored before reaching your server.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Why it's critical
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "WHY MERCHANTS NEED THIS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero Privacy Leaks: Personal chats, family SMS, and sensitive two-factor authentication (2FA) OTPs are NEVER forwarded.",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "No Fake Payment Spams: Telecom promo messages or unknown numbers cannot spam your webhook and trigger illegitimate order completions.",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Syntax Format
                Text(
                    text = "Supported Syntax",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter a comma-separated list of sender names or shortcodes (case-insensitive):",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "bKash, NAGAD, Rocket, Upay, CityBank, 10606",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldDark
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // One-Click Apply Button
                Button(
                    onClick = {
                        onApplyRecommended("bKash, NAGAD, Rocket, Upay, CityBank, EBL, 10606")
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = ObsidianBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Apply Recommended Whitelist",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
