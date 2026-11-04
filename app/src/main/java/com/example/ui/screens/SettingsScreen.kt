package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AppSettingsEntity
import com.example.ui.components.ButtonVariant
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WisfyShapes
import com.example.ui.theme.WisfySpacing

@Composable
fun SettingsScreen(
    settings: AppSettingsEntity?,
    eventCount: Int,
    onToggleService: (Boolean) -> Unit,
    onToggleMaskBody: (Boolean) -> Unit,
    onToggleForwardRaw: (Boolean) -> Unit,
    onToggleOtpFilter: (Boolean) -> Unit,
    onClearData: () -> Unit,
    onExportReport: ((String) -> Unit) -> Unit,
    onOpenDocs: () -> Unit,
    onSelectThemeMode: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val colors = LocalWisfyColors.current
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = WisfySpacing.screenPadding)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Settings",
            fontFamily = InterFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            letterSpacing = (-0.4).sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Local execution, theme and pipeline rules",
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Appearance & Theme (Graphite Segmented Control)
        WisfyLayeredCard(
            icon = Icons.Default.Palette,
            iconTint = colors.textPrimary,
            title = "Appearance & Theme"
        ) {
            val currentMode = settings?.themeMode ?: "LIGHT"

            Text(
                text = "Interface Style",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Segmented pill control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WisfyShapes.Pill)
                    .background(colors.surfaceHighlight)
                    .border(1.dp, colors.border, WisfyShapes.Pill)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "LIGHT" to "Light",
                    "DARK" to "Dark",
                    "SYSTEM" to "System"
                ).forEach { (key, label) ->
                    val isSelected = currentMode == key
                    val isDark = colors.isDark

                    val bg = if (isSelected) {
                        if (isDark) Color(0xFFFAFAFA) else Color(0xFF1A1A1C)
                    } else Color.Transparent

                    val fg = if (isSelected) {
                        if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
                    } else colors.textSecondary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(WisfyShapes.Pill)
                            .background(bg)
                            .clickable { onSelectThemeMode(key) }
                            .padding(vertical = 9.dp)
                            .testTag("theme_option_${key.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = fg
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Service & Background Daemon
        WisfyLayeredCard(
            icon = Icons.Default.PowerSettingsNew,
            iconTint = colors.emerald,
            title = "SMS Forwarding Daemon"
        ) {
            SettingsToggleRow(
                title = "Background Service",
                subtitle = "Forward incoming SMS in real-time even when app is closed",
                checked = settings?.backgroundServiceActive ?: true,
                onCheckedChange = onToggleService,
                testTag = "settings_toggle_service"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Privacy & Security
        WisfyLayeredCard(
            icon = Icons.Default.Shield,
            iconTint = colors.statusDuplicate,
            title = "Privacy & Filtering"
        ) {
            SettingsToggleRow(
                title = "Mask Sensitive Text",
                subtitle = "Mask card numbers, PINs, and personal account digits in webhooks",
                checked = settings?.maskSmsBodyInLogs ?: true,
                onCheckedChange = onToggleMaskBody,
                testTag = "settings_toggle_mask"
            )

            Spacer(modifier = Modifier.height(14.dp))

            SettingsToggleRow(
                title = "Filter One-Time Passwords (OTP)",
                subtitle = "Drop transient 2FA and OTP messages from webhook forwarding",
                checked = settings?.autoFilterOtp ?: false,
                onCheckedChange = onToggleOtpFilter,
                testTag = "settings_toggle_otp"
            )

            Spacer(modifier = Modifier.height(14.dp))

            SettingsToggleRow(
                title = "Include Raw Body",
                subtitle = "Forward raw unchanged SMS text alongside structured data",
                checked = settings?.forwardRawMessageBody ?: true,
                onCheckedChange = onToggleForwardRaw,
                testTag = "settings_toggle_forward_raw"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Data Management & Export
        WisfyLayeredCard(
            icon = Icons.Default.FileDownload,
            iconTint = colors.statusPending,
            title = "Data Management"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Export Dispatch Log",
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Export all logged events as JSON format",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(WisfyShapes.Pill)
                        .background(colors.surfaceHighlight)
                        .border(1.dp, colors.border, WisfyShapes.Pill)
                        .clickable {
                            onExportReport { json ->
                                Toast.makeText(context, "Exported JSON (${json.length} bytes)", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Export",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Clear Local History",
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ErrorRed
                    )
                    Text(
                        text = "Delete all stored events and logs permanently",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(WisfyShapes.Pill)
                        .background(Color(0xFFFEE2E2))
                        .border(1.dp, ErrorRed.copy(alpha = 0.3f), WisfyShapes.Pill)
                        .clickable { showClearDialog = true }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Clear",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ErrorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. About & Developer Credits
        WisfyLayeredCard(
            icon = Icons.Default.Info,
            title = "About Wisfy SMS"
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Version",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "2.4.0 (Graphite Wallet)",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Developer",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Shahria Nafiz Sajjad",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "License",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Open Source (MIT)",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear All Records?",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently erase all captured SMS events and dispatch logs from this device. Webhook endpoints will be kept.",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                WisfyButton(
                    text = "Clear Data",
                    variant = ButtonVariant.DESTRUCTIVE,
                    onClick = {
                        onClearData()
                        showClearDialog = false
                    }
                )
            },
            dismissButton = {
                WisfyButton(
                    text = "Cancel",
                    variant = ButtonVariant.GHOST,
                    onClick = { showClearDialog = false }
                )
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val colors = LocalWisfyColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                color = colors.textSecondary,
                lineHeight = 16.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF111113),
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.surfaceHighlight
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
