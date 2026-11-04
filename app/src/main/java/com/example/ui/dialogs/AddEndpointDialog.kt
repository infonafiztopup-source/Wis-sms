package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.ui.theme.LocalWisfyColors
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
    val isDark = LocalWisfyColors.current.isDark
    var name by remember { mutableStateOf(initialEndpoint?.name ?: "") }
    var url by remember { mutableStateOf(initialEndpoint?.url ?: "") }
    var token by remember { mutableStateOf(initialEndpoint?.authToken ?: "") }
    var showToken by remember { mutableStateOf(false) }
    var senderFilter by remember { mutableStateOf(initialEndpoint?.senderFilter ?: "") }
    var isWhitelistDropdownOpen by remember { mutableStateOf(true) }
    var customSenderInput by remember { mutableStateOf("") }
    var providerFilterTab by remember { mutableStateOf("ALL") } // "ALL", "MFS", "BANK"
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
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.96f),
            shape = com.example.ui.theme.WisfyShapes.BottomSheetTop,
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

                // Field 4: Sender Whitelist Filter (Premium Responsive Redesign)
                val selectedSenders = remember(senderFilter) {
                    if (senderFilter.isBlank()) emptyList()
                    else senderFilter.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                }

                data class ProviderItem(
                    val name: String,
                    val category: String,
                    val code: String,
                    val brandColor: Color
                )

                val defaultProviders = remember {
                    listOf(
                        ProviderItem("bKash", "MFS", "BK", Color(0xFFE2136E)),
                        ProviderItem("Nagad", "MFS", "NG", Color(0xFFF7941D)),
                        ProviderItem("Rocket", "MFS", "RK", Color(0xFF8C3494)),
                        ProviderItem("Upay", "MFS", "UP", Color(0xFF0072BC)),
                        ProviderItem("Cellfin", "Bank", "CF", Color(0xFF00897B)),
                        ProviderItem("City Bank", "Bank", "CB", Color(0xFFD32F2F)),
                        ProviderItem("DBBL", "Bank", "DB", Color(0xFF1565C0)),
                        ProviderItem("Islami Bank", "Bank", "IB", Color(0xFF2E7D32))
                    )
                }

                val toggleProvider: (String) -> Unit = { name ->
                    val exists = selectedSenders.any { it.equals(name, ignoreCase = true) }
                    val updated = if (exists) {
                        selectedSenders.filterNot { it.equals(name, ignoreCase = true) }
                    } else {
                        selectedSenders + name
                    }
                    senderFilter = updated.joinToString(", ")
                }

                val addCustomSender: () -> Unit = {
                    val raw = customSenderInput.trim().replace(",", "")
                    if (raw.isNotEmpty()) {
                        if (!selectedSenders.any { it.equals(raw, ignoreCase = true) }) {
                            val updated = selectedSenders + raw
                            senderFilter = updated.joinToString(", ")
                        }
                        customSenderInput = ""
                    }
                }

                val removeSender: (String) -> Unit = { name ->
                    val updated = selectedSenders.filterNot { it.equals(name, ignoreCase = true) }
                    senderFilter = updated.joinToString(", ")
                }

                val selectAllMfs: () -> Unit = {
                    val mfsList = listOf("bKash", "Nagad", "Rocket", "Upay")
                    val currentList = selectedSenders.toMutableList()
                    mfsList.forEach { mfs ->
                        if (!currentList.any { it.equals(mfs, ignoreCase = true) }) {
                            currentList.add(mfs)
                        }
                    }
                    senderFilter = currentList.joinToString(", ")
                }

                val clearAllSenders: () -> Unit = {
                    senderFilter = ""
                }

                val displayedProviders = remember(providerFilterTab) {
                    when (providerFilterTab) {
                        "MFS" -> defaultProviders.filter { it.category == "MFS" }
                        "BANK" -> defaultProviders.filter { it.category == "Bank" }
                        else -> defaultProviders
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    // Header row (Click to expand/collapse dropdown)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWhitelistDropdownOpen = !isWhitelistDropdownOpen },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sender Whitelist Filter",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                                    .clickable { showWhitelistInfoDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Sender Whitelist Info",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (selectedSenders.isEmpty()) (if (isDark) Color(0xFF161F2C) else Color(0xFFF1F5F9)) else EmeraldLight)
                                    .border(
                                        1.dp,
                                        if (selectedSenders.isEmpty()) ObsidianBorder else EmeraldGreen.copy(alpha = 0.4f),
                                        RoundedCornerShape(9999.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedSenders.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldGreen)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                    }
                                    Text(
                                        text = if (selectedSenders.isEmpty()) "ALL ALLOWED" else "${selectedSenders.size} ACTIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (selectedSenders.isEmpty()) TextSecondary else (if (isDark) Color(0xFF34D399) else Color(0xFF047857))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (isWhitelistDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isWhitelistDropdownOpen) "Collapse" else "Expand",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Active Selected Chips Bar (Removable)
                    if (selectedSenders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedSenders.forEach { sender ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(if (isDark) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFECFDF5))
                                        .border(1.dp, EmeraldGreen.copy(alpha = 0.45f), RoundedCornerShape(9999.dp))
                                        .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sender,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color(0xFF065F46) else Color(0xFFD1FAE5))
                                            .clickable { removeSender(sender) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove $sender",
                                            tint = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No filter active — all incoming messages will be forwarded.",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }

                    // Dropdown Checklist & Custom Add Section
                    AnimatedVisibility(visible = isWhitelistDropdownOpen) {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                            HorizontalDivider(color = ObsidianBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Segmented Filter Tabs & Quick Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category Pill Tabs
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDark) Color(0xFF0C1017) else Color(0xFFF1F5F9))
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    listOf("ALL" to "All (8)", "MFS" to "MFS (4)", "BANK" to "Banks (4)").forEach { (key, label) ->
                                        val isSelectedTab = providerFilterTab == key
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isSelectedTab) {
                                                        if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
                                                    } else Color.Transparent
                                                )
                                                .clickable { providerFilterTab = key }
                                                .padding(horizontal = 9.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.5.sp,
                                                fontWeight = if (isSelectedTab) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelectedTab) TextPrimary else TextSecondary
                                            )
                                        }
                                    }
                                }

                                // Quick Action Buttons
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldLight)
                                            .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .clickable { selectAllMfs() }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Select MFS",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF34D399) else Color(0xFF047857)
                                        )
                                    }

                                    if (selectedSenders.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ErrorRed.copy(alpha = 0.1f))
                                            .border(1.dp, ErrorRed.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                            .clickable { clearAllSenders() }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = ErrorRed,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Clear All",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ErrorRed
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Provider Grid with Branded Monogram Badges & Theme-Adaptive Cards
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                displayedProviders.chunked(2).forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowItems.forEach { provider ->
                                            val isChecked = selectedSenders.any { it.equals(provider.name, ignoreCase = true) }
                                            val cardBg = if (isChecked) {
                                                if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFECFDF5)
                                            } else {
                                                if (isDark) Color(0xFF121822) else Color(0xFFFFFFFF)
                                            }
                                            val cardBorder = if (isChecked) {
                                                EmeraldGreen
                                            } else {
                                                if (isDark) Color(0xFF233042) else Color(0xFFE2E8F0)
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .heightIn(min = 52.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(cardBg)
                                                    .border(if (isChecked) 1.5.dp else 1.dp, cardBorder, RoundedCornerShape(10.dp))
                                                    .clickable { toggleProvider(provider.name) }
                                                    .padding(horizontal = 9.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f, fill = false),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Branded Monogram Badge
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(provider.brandColor.copy(alpha = if (isChecked) 0.22f else 0.12f))
                                                            .border(1.dp, provider.brandColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = provider.code,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontFamily = FontFamily.Monospace,
                                                            color = provider.brandColor
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(7.dp))
                                                    Column {
                                                        Text(
                                                            text = provider.name,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                                            color = if (isChecked) (if (isDark) Color(0xFF34D399) else Color(0xFF065F46)) else TextPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = provider.category,
                                                            fontSize = 9.sp,
                                                            fontFamily = FontFamily.Monospace,
                                                            color = if (isChecked) (if (isDark) Color(0xFF34D399).copy(alpha = 0.8f) else Color(0xFF047857)) else TextSecondary,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }

                                                // Selection check indicator
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isChecked) EmeraldGreen else Color.Transparent)
                                                        .border(
                                                            1.5.dp,
                                                            if (isChecked) EmeraldGreen else (if (isDark) Color(0xFF334358) else Color(0xFFCBD5E1)),
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isChecked) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = PureWhite,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = ObsidianBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Custom Sender Input (Responsive & Theme Adaptive)
                            Text(
                                text = "ADD CUSTOM SENDER / NUMBER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.6.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customSenderInput,
                                    onValueChange = { customSenderInput = it },
                                    placeholder = {
                                        Text(
                                            "e.g. 16216, 01712345678, BRAC",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.FilterAlt,
                                            contentDescription = null,
                                            tint = if (customSenderInput.isNotBlank()) EmeraldGreen else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("endpoint_filter_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = ObsidianCard,
                                        unfocusedContainerColor = ObsidianCard,
                                        focusedBorderColor = EmeraldGreen,
                                        unfocusedBorderColor = ObsidianBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Button(
                                    onClick = { addCustomSender() },
                                    enabled = customSenderInput.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldGreen,
                                        contentColor = PureWhite,
                                        disabledContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                        disabledContentColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("add_custom_sender_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (customSenderInput.isNotBlank()) PureWhite else (if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (customSenderInput.isNotBlank()) PureWhite else (if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8))
                                    )
                                }
                            }
                        }
                    }
                }

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

                // 3 Segmented SIM Selection Pills with responsive flex and theme adaptive colors
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
                            .background(
                                if (isBoth) {
                                    if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFECFDF5)
                                } else {
                                    if (isDark) Color(0xFF161F2C) else Color(0xFFFFFFFF)
                                }
                            )
                            .border(
                                if (isBoth) 1.5.dp else 1.dp,
                                if (isBoth) EmeraldGreen else (if (isDark) Color(0xFF233042) else Color(0xFFE2E8F0)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { simSlot = 0 }
                            .padding(vertical = 11.dp, horizontal = 6.dp)
                            .testTag("sim_selection_both"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isBoth) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Both SIMs",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBoth) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "All incoming",
                                fontSize = 9.5.sp,
                                color = if (isBoth) (if (isDark) Color(0xFF34D399).copy(alpha = 0.8f) else Color(0xFF059669)) else TextSecondary,
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
                            .background(
                                if (isSim1) {
                                    if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFECFDF5)
                                } else {
                                    if (isDark) Color(0xFF161F2C) else Color(0xFFFFFFFF)
                                }
                            )
                            .border(
                                if (isSim1) 1.5.dp else 1.dp,
                                if (isSim1) EmeraldGreen else (if (isDark) Color(0xFF233042) else Color(0xFFE2E8F0)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { simSlot = 1 }
                            .padding(vertical = 11.dp, horizontal = 6.dp)
                            .testTag("sim_selection_sim1"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isSim1) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM 1 Only",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSim1) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Primary Slot",
                                fontSize = 9.5.sp,
                                color = if (isSim1) (if (isDark) Color(0xFF34D399).copy(alpha = 0.8f) else Color(0xFF059669)) else TextSecondary,
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
                            .background(
                                if (isSim2) {
                                    if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFECFDF5)
                                } else {
                                    if (isDark) Color(0xFF161F2C) else Color(0xFFFFFFFF)
                                }
                            )
                            .border(
                                if (isSim2) 1.5.dp else 1.dp,
                                if (isSim2) EmeraldGreen else (if (isDark) Color(0xFF233042) else Color(0xFFE2E8F0)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { simSlot = 2 }
                            .padding(vertical = 11.dp, horizontal = 6.dp)
                            .testTag("sim_selection_sim2"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = if (isSim2) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM 2 Only",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSim2) (if (isDark) Color(0xFF34D399) else Color(0xFF047857)) else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Secondary Slot",
                                fontSize = 9.5.sp,
                                color = if (isSim2) (if (isDark) Color(0xFF34D399).copy(alpha = 0.8f) else Color(0xFF059669)) else TextSecondary,
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
                            if (isDark) {
                                Brush.verticalGradient(listOf(Color(0xFF334358), ObsidianBorder))
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFFE2E8F0)))
                            },
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
