package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.components.WisfyBrandShape
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.PureWhite

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
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Large Bold Title & Subtitle
        Text(
            text = "Settings & Rules",
            fontFamily = InterFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Local execution, retry policies and on-device telemetry",
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            color = colors.textSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 0: App Theme Appearance
        SectionHeader(icon = Icons.Default.Tune, title = "APPEARANCE & THEME")
        WisfyCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Theme Preference",
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Select application visual style",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(colors.statusSuccessBg)
                            .border(1.dp, colors.statusSuccess.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = (settings?.themeMode ?: "LIGHT").uppercase(),
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = colors.statusSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val currentMode = settings?.themeMode ?: "LIGHT"
                // Segmented Theme Selector Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceLow)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modes = listOf(
                        Triple("LIGHT", "Light", "Crisp neutral"),
                        Triple("DARK", "Dark", "Obsidian charcoal"),
                        Triple("SYSTEM", "Auto", "Follow OS")
                    )

                    modes.forEach { (modeKey, modeTitle, modeSubtitle) ->
                        val isSelected = currentMode == modeKey

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) {
                                        if (colors.isDark) Color(0xFF34D399) else Color(0xFF202A23)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable { onSelectThemeMode(modeKey) }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                                .testTag("theme_option_${modeKey.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = modeTitle,
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PureWhite else colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = modeSubtitle,
                                    fontFamily = InterFontFamily,
                                    fontSize = 10.sp,
                                    color = if (isSelected) PureWhite.copy(alpha = 0.85f) else colors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section 1: Listener & Background Execution
        SectionHeader(icon = Icons.Default.BatterySaver, title = "LISTENER & BACKGROUND EXECUTION")
        WisfyCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Background Daemon",
                                fontFamily = InterFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (settings?.backgroundServiceActive == true) colors.statusSuccessBg else colors.surfaceLow)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (settings?.backgroundServiceActive == true) "ACTIVE" else "STOPPED",
                                    fontSize = 9.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings?.backgroundServiceActive == true) colors.statusSuccess else colors.textMuted
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Persistent foreground daemon capturing SMS broadcasts",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 17.sp
                        )
                    }

                    Switch(
                        checked = settings?.backgroundServiceActive ?: true,
                        onCheckedChange = onToggleService,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = EmeraldGreen,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.surfaceLow,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("toggle_background_service")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderSubtle))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Battery Optimization",
                                fontFamily = InterFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Exempted from aggressive OS Doze mode suspension",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 17.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(colors.surfaceLow)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(9999.dp))
                            .clickable {
                                Toast.makeText(context, "Battery optimization exemption active", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Active",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section 2: Webhook & Retry Rules
        SectionHeader(icon = Icons.Default.Hub, title = "WEBHOOK & RETRY RULES")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WisfyCard(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RETRY POLICY",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textMuted,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "${settings?.retryCount ?: 3} Retries",
                        fontFamily = InterFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Exponential backoff\n1s • 2s • 4s intervals",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = colors.textSecondary
                    )
                }
            }

            WisfyCard(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TIMEOUT CAP",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textMuted,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "${settings?.timeoutSeconds ?: 10} Seconds",
                        fontFamily = InterFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Strict HTTP & socket\nconnection limit",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section 3: Privacy & Data Minimization
        SectionHeader(icon = Icons.Default.Shield, title = "PRIVACY & DATA MINIMIZATION")
        WisfyCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Toggle 1: Mask Body in logs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Mask SMS Body in Logs",
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Obfuscates message text in local database with asterisks",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 17.sp
                        )
                    }
                    Switch(
                        checked = settings?.maskSmsBodyInLogs ?: true,
                        onCheckedChange = onToggleMaskBody,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = EmeraldGreen,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.surfaceLow,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("toggle_mask_body")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderSubtle))
                Spacer(modifier = Modifier.height(14.dp))

                // Toggle 2: Forward raw body
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Forward Raw Message Body",
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Disabled: transmits sanitized/parsed JSON payloads only",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 17.sp
                        )
                    }
                    Switch(
                        checked = settings?.forwardRawMessageBody ?: false,
                        onCheckedChange = onToggleForwardRaw,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = EmeraldGreen,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.surfaceLow,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("toggle_forward_raw")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderSubtle))
                Spacer(modifier = Modifier.height(14.dp))

                // Toggle 3: OTP Auto-filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "OTP / Private Code Auto-Filter",
                                fontFamily = InterFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.surfaceLow)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Regex v2",
                                    fontSize = 9.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = colors.textMuted
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Redacts one-time verification tokens before logging",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 17.sp
                        )
                    }
                    Switch(
                        checked = settings?.autoFilterOtp ?: true,
                        onCheckedChange = onToggleOtpFilter,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = EmeraldGreen,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.surfaceLow,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("toggle_otp_filter")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section 4: Local Data & Diagnostics
        SectionHeader(icon = Icons.Default.Storage, title = "LOCAL DATA & CACHE")
        WisfyCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SQLite On-Device Storage",
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Zero-cloud device buffer & delivery audit logs",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$eventCount records",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Local DB",
                            fontFamily = InterFontFamily,
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Export Diagnostic Report & Clear Data
                WisfyButton(
                    text = "Export Diagnostic Report (Redacted)",
                    icon = Icons.Default.FileDownload,
                    onClick = {
                        onExportReport { reportJson ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Wisfy Diagnostic Report", reportJson))
                            Toast.makeText(context, "Redacted diagnostic report copied to clipboard", Toast.LENGTH_LONG).show()
                        }
                    },
                    variant = ButtonVariant.PRIMARY,
                    height = 42.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_report_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                WisfyButton(
                    text = "Clear Local Queue & Logs",
                    icon = Icons.Default.ClearAll,
                    variant = ButtonVariant.DESTRUCTIVE,
                    onClick = { showClearDialog = true },
                    height = 42.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_data_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section 5: About & Open Source Attribution (Borderless brand shape)
        SectionHeader(icon = Icons.Default.Info, title = "ABOUT & OPEN SOURCE")
        WisfyCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        WisfyBrandShape(
                            modifier = Modifier.size(38.dp),
                            contentDescription = "Wisfy Logo"
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Wisfy SMS",
                                    fontFamily = InterFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.surfaceLow)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "v1.0 FOSS",
                                        fontSize = 9.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textMuted
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Developed by Shahria Nafiz Sajjad",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    WisfyButton(
                        text = "API Docs",
                        onClick = onOpenDocs,
                        variant = ButtonVariant.OUTLINE,
                        height = 36.dp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Open-source, backend-free SMS-to-webhook bridge for automated payment notifications, merchant APIs, and custom telemetry workflows.",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = colors.surfaceElevated,
            title = {
                Text(
                    text = "Clear All Local Logs?",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "This will permanently erase all received SMS records, delivery logs, and queues stored locally on this phone.",
                    fontFamily = InterFontFamily,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                WisfyButton(
                    text = "Clear All",
                    variant = ButtonVariant.DESTRUCTIVE,
                    height = 36.dp,
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
                    height = 36.dp,
                    onClick = { showClearDialog = false }
                )
            }
        )
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    val colors = LocalWisfyColors.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EmeraldDark,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textMuted,
            letterSpacing = 1.1.sp
        )
    }
}
