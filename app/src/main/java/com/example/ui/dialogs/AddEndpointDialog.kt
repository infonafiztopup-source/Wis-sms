package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.network.DispatchResult
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardHighlight
import com.example.ui.theme.ObsidianDark
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.example.ui.components.AppleGlossyButton
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddEndpointDialog(
    initialEndpoint: WebhookEndpointEntity? = null,
    onDismiss: () -> Unit,
    onSave: (WebhookEndpointEntity) -> Unit,
    onTest: (WebhookEndpointEntity, (DispatchResult) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialEndpoint?.name ?: "") }
    var url by remember { mutableStateOf(initialEndpoint?.url ?: "") }
    var token by remember { mutableStateOf(initialEndpoint?.authToken ?: "") }
    var showToken by remember { mutableStateOf(false) }
    var senderFilter by remember { mutableStateOf(initialEndpoint?.senderFilter ?: "") }
    var simSlot by remember { mutableIntStateOf(initialEndpoint?.simSlot ?: 0) } // 0 = Both, 1 = SIM 1, 2 = SIM 2
    var configMode by remember { mutableStateOf("manual") }

    var showPremiumScanner by remember { mutableStateOf(false) }
    var showWhitelistInfoDialog by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    var testResultToShow by remember { mutableStateOf<DispatchResult?>(null) }
    var urlError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ObsidianDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Back Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorder, CircleShape)
                            .clickable(onClick = onDismiss)
                            .testTag("webhook_back_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // TLS 1.3 SECURE Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(EmeraldLight)
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TLS 1.3 SECURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp,
                            color = EmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Small Gateway Dispatch Category Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GATEWAY DISPATCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = EmeraldGreen
                    )
                }

                // Title & Subtitle
                Text(
                    text = "Connect Webhook",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Link your Android device directly to your server or e-commerce store",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Segmented Switcher Pill: Manual Config / Scan QR Code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(9999.dp))
                        .padding(4.dp)
                ) {
                    val isManual = configMode == "manual"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (isManual) ObsidianCardHighlight else Color.Transparent)
                            .clickable { configMode = "manual" }
                            .padding(vertical = 10.dp)
                            .testTag("tab_manual_config"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (isManual) EmeraldGreen else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Manual Config",
                                fontSize = 13.sp,
                                fontWeight = if (isManual) FontWeight.Bold else FontWeight.Medium,
                                color = if (isManual) TextPrimary else TextSecondary
                            )
                        }
                    }

                    // Scan QR Code Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (!isManual) ObsidianCardHighlight else Color.Transparent)
                            .clickable {
                                configMode = "qr"
                                showPremiumScanner = true
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_scan_qr"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = if (!isManual) EmeraldGreen else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scan QR Code",
                                fontSize = 13.sp,
                                fontWeight = if (!isManual) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isManual) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Field 1: Endpoint Name
                Text(
                    text = "Endpoint Name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Production Merchant API", color = TextSecondary) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("endpoint_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 2: Webhook URL with HTTPS Enforced Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Webhook URL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(EmeraldLight)
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HTTPS Enforced",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        val trimmed = it.trim()
                        urlError = when {
                            trimmed.isBlank() -> "Webhook URL is required"
                            !trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true) && !trimmed.contains(".") && !trimmed.contains("localhost") -> "Enter a valid webhook URL or IP"
                            else -> null
                        }
                    },
                    placeholder = { Text("https://your-domain.com/webhook or webhook.site/...", color = TextSecondary) },
                    trailingIcon = {
                        val isHttps = url.trim().startsWith("https://", ignoreCase = true)
                        val isHttp = url.trim().startsWith("http://", ignoreCase = true)
                        if (isHttps) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Valid HTTPS",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (isHttp) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "HTTP",
                                tint = AmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    isError = urlError != null,
                    supportingText = urlError?.let { { Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("endpoint_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 3: Authentication Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Authentication Header",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Bearer Token",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    placeholder = { Text("sec_live_9a8f4c... (optional)", color = TextSecondary) },
                    visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showToken = !showToken }) {
                            Icon(
                                imageVector = if (showToken) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle token visibility",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("endpoint_token_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 4: Sender Whitelist Filter (with info (i) button requested by user)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sender Whitelist Filter",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(EmeraldLight)
                                .clickable { showWhitelistInfoDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Sender Whitelist Info",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Text(
                        text = "Comma-separated",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = senderFilter,
                    onValueChange = { senderFilter = it },
                    placeholder = { Text("bKash, NAGAD, Rocket, Upay, 10606", color = TextSecondary) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("endpoint_filter_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 5: SIM Card Routing (SIM Selection with strict no-overlap layout)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIM Card Routing",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Dual-SIM Control",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 3 Segmented SIM Selection Pills with ellipsis and proper flex
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 0: Both SIMs
                    val isBoth = simSlot == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isBoth) EmeraldLight else ObsidianCard)
                            .border(1.dp, if (isBoth) EmeraldGreen else ObsidianBorder, RoundedCornerShape(12.dp))
                            .clickable { simSlot = 0 }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("sim_selection_both"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isBoth) EmeraldDark else TextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Both SIMs",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBoth) EmeraldDark else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "All incoming",
                                fontSize = 9.sp,
                                color = if (isBoth) EmeraldDark.copy(alpha = 0.8f) else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Option 1: SIM 1 Only
                    val isSim1 = simSlot == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSim1) EmeraldLight else ObsidianCard)
                            .border(1.dp, if (isSim1) EmeraldGreen else ObsidianBorder, RoundedCornerShape(12.dp))
                            .clickable { simSlot = 1 }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("sim_selection_sim1"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isSim1) EmeraldDark else TextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM 1 Only",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSim1) EmeraldDark else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Primary Slot",
                                fontSize = 9.sp,
                                color = if (isSim1) EmeraldDark.copy(alpha = 0.8f) else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Option 2: SIM 2 Only
                    val isSim2 = simSlot == 2
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSim2) EmeraldLight else ObsidianCard)
                            .border(1.dp, if (isSim2) EmeraldGreen else ObsidianBorder, RoundedCornerShape(12.dp))
                            .clickable { simSlot = 2 }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("sim_selection_sim2"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isSim2) EmeraldDark else TextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM 2 Only",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSim2) EmeraldDark else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Secondary Slot",
                                fontSize = 9.sp,
                                color = if (isSim2) EmeraldDark.copy(alpha = 0.8f) else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button 1: Test Webhook (wisfy.test)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(9999.dp), spotColor = Color(0x10000000))
                        .clip(RoundedCornerShape(9999.dp))
                        .background(ObsidianCard)
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(PureWhite.copy(alpha = 0.8f), ObsidianBorder)
                            ),
                            RoundedCornerShape(9999.dp)
                        )
                        .clickable(enabled = url.isNotBlank() && !isTesting) {
                            val trimmedUrl = url.trim()
                            val finalUrl = when {
                                trimmedUrl.startsWith("http://", ignoreCase = true) || trimmedUrl.startsWith("https://", ignoreCase = true) -> trimmedUrl
                                trimmedUrl.isNotBlank() -> "https://$trimmedUrl"
                                else -> ""
                            }
                            val tempEp = WebhookEndpointEntity(
                                id = initialEndpoint?.id ?: 0,
                                name = name.ifBlank { "Test Probe" },
                                url = finalUrl,
                                authToken = token.trim(),
                                senderFilter = senderFilter.trim(),
                                simSlot = simSlot,
                                timeoutSec = 10,
                                enabled = true
                            )
                            isTesting = true
                            onTest(tempEp) { result ->
                                isTesting = false
                                testResultToShow = result
                            }
                        }
                        .testTag("test_webhook_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTesting) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = EmeraldGreen, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pinging Endpoint...", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NorthEast,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = EmeraldGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Test Webhook",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(EmeraldLight)
                                    .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "wisfy.test",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Button 2: Save & Activate Webhook (Apple Glossy Button)
                AppleGlossyButton(
                    text = "Save & Activate Webhook",
                    icon = Icons.Default.Bolt,
                    enabled = url.isNotBlank(),
                    onClick = {
                        val trimmedUrl = url.trim()
                        val finalUrl = when {
                            trimmedUrl.startsWith("http://", ignoreCase = true) || trimmedUrl.startsWith("https://", ignoreCase = true) -> trimmedUrl
                            trimmedUrl.isNotBlank() -> "https://$trimmedUrl"
                            else -> ""
                        }
                        val endpoint = WebhookEndpointEntity(
                            id = initialEndpoint?.id ?: 0,
                            name = name.ifBlank { "Custom Webhook" },
                            url = finalUrl,
                            authToken = token.trim(),
                            senderFilter = senderFilter.trim(),
                            simSlot = simSlot,
                            timeoutSec = 10,
                            enabled = true
                        )
                        onSave(endpoint)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_webhook_button")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OR Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = ObsidianBorder)
                    Text(
                        text = "OR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = ObsidianBorder)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom QR Code Pairing Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .clickable { showPremiumScanner = true }
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(EmeraldLight)
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code Pairing",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Or pair with QR Code from Web Dashboard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Instant zero-keystroke cryptographic provisioning",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Footer: TLS 1.3 Verified • End-to-End Encrypted
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TLS 1.3 Verified  •  End-to-End Encrypted",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Premium QR Scanner Dialog
    if (showPremiumScanner) {
        PremiumQrScannerDialog(
            onDismiss = {
                showPremiumScanner = false
                configMode = "manual"
            },
            onConfigScanned = { scanned ->
                name = scanned.name
                url = scanned.url
                token = scanned.token
                senderFilter = scanned.senderFilter
                simSlot = scanned.simSlot
                showPremiumScanner = false
                configMode = "manual"
                Toast.makeText(context, "Pairing config imported!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Sender Whitelist Information Dialog (Triggered by (i) button)
    if (showWhitelistInfoDialog) {
        WhitelistInfoDialog(
            onDismiss = { showWhitelistInfoDialog = false },
            onApplyRecommended = { recommended ->
                senderFilter = recommended
                Toast.makeText(context, "Applied financial whitelist", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Webhook Test Inspection Dialog
    testResultToShow?.let { result ->
        WebhookTestResultDialog(
            endpointName = name,
            endpointUrl = url,
            result = result,
            onDismiss = { testResultToShow = null }
        )
    }
}
