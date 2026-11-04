package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.domain.model.WisfyPayload
import com.example.ui.components.WisfyCtaButton
import com.example.ui.components.WisfyDeltaChip
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.components.WisfyThinProgress
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfyShapes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Goals Detail-inspired Payload Inspector (Reference: Salung Goals Detail)
 */
@Composable
fun InspectPayloadDialog(
    event: SmsEventEntity,
    log: DeliveryLogEntity?,
    onDismiss: () -> Unit,
    onRetry: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val colors = LocalWisfyColors.current
    var isRetrying by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Parsed") }

    val payload = WisfyPayload(
        event = "sms.received",
        eventType = event.eventType,
        eventId = event.eventId,
        schemaVersion = "1.1",
        deviceId = event.deviceId,
        receivedAtMs = event.receivedAt,
        sender = event.sender,
        body = event.sanitizedBody,
        provider = event.provider,
        transactionId = event.transactionId,
        amount = event.amount,
        currency = event.currency,
        status = event.status,
        idempotencyKey = event.idempotencyKey,
        isMessageDuplicate = event.isMessageDuplicate,
        isTransactionDuplicate = event.isTransactionDuplicate,
        senderMatch = event.senderMatch,
        templateMatch = event.templateMatch,
        serverVerified = false
    )
    val jsonString = payload.toJsonString()
    val timeFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.US) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = WisfyShapes.BottomSheetTop,
            color = colors.canvasBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top Action Bar: Back chevron, centered title, close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }

                    Text(
                        text = "Event Detail",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Header Block: Icon tile + Status Delta Chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WisfyIconTile(
                            icon = Icons.Default.Sms,
                            tint = colors.emerald,
                            containerColor = colors.surfaceCard,
                            size = 48.dp
                        )

                        WisfyDeltaChip(
                            text = (log?.status ?: event.status).uppercase(),
                            isPositive = log?.status == "delivered" || event.status == "delivered",
                            leadingArrow = log?.status == "delivered"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Amount & Sender Subtitle
                    Text(
                        text = if (event.amount != null) "৳ ${event.amount}" else event.sender,
                        fontFamily = InterFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = (-0.6).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${event.sender} · ${timeFormat.format(Date(event.receivedAt))}",
                        fontFamily = InterFontFamily,
                        fontSize = 13.5.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress / Attempt Stats Card
                    WisfyLayeredCard(
                        title = "Delivery Progress"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Attempt ${log?.attemptCount ?: 1} of 3",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${log?.httpStatusCode ?: 200} HTTP",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = if (log?.status == "delivered") colors.emerald else colors.statusFailed
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        WisfyThinProgress(
                            progress = if (log?.status == "delivered") 1.0f else 0.33f,
                            fillColor = if (log?.status == "delivered") colors.emerald else colors.statusPending
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (log?.status == "delivered") "Webhook accepted payload successfully" else (log?.errorMessage ?: "Pending transmission"),
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payload Content Card with Segmented Tabs
                    WisfyLayeredCard(
                        title = "Payload Inspection"
                    ) {
                        // Tabs: Parsed / JSON / Raw
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(WisfyShapes.Pill)
                                .background(colors.surfaceHighlight)
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Parsed", "JSON", "Raw").forEach { tab ->
                                val isSelected = selectedTab == tab
                                val bg = if (isSelected) {
                                    if (colors.isDark) Color(0xFFFAFAFA) else Color(0xFF1A1A1C)
                                } else Color.Transparent
                                val fg = if (isSelected) {
                                    if (colors.isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
                                } else colors.textSecondary

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(WisfyShapes.Pill)
                                        .background(bg)
                                        .clickable { selectedTab = tab }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab,
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = fg
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (selectedTab) {
                            "Parsed" -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    DetailMetaRow("Event ID", event.eventId)
                                    DetailMetaRow("Sender", event.sender)
                                    DetailMetaRow("TrxID", event.transactionId ?: "None")
                                    DetailMetaRow("Amount", event.amount?.toString() ?: "None")
                                    DetailMetaRow("Currency", event.currency ?: "BDT")
                                    DetailMetaRow("Provider", event.provider ?: "Generic")
                                }
                            }
                            "JSON" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.surfaceHighlight)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = jsonString,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 11.sp,
                                        color = colors.textPrimary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                            "Raw" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.surfaceHighlight)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = event.rawBody,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 11.5.sp,
                                        color = colors.textPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Bottom Floating CTA Button
                WisfyCtaButton(
                    text = if (log?.id != null && onRetry != null) "Retry delivery" else "Close inspector",
                    onClick = {
                        if (log?.id != null && onRetry != null) {
                            onRetry(log.id)
                            onDismiss()
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                    testTag = "inspect_retry_btn"
                )
            }
        }
    }
}

@Composable
private fun DetailMetaRow(label: String, value: String) {
    val colors = LocalWisfyColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
        Text(
            text = value,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}
