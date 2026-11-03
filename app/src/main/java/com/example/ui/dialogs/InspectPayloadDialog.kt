package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.domain.model.WisfyPayload
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardHighlight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InspectPayloadDialog(
    event: SmsEventEntity,
    log: DeliveryLogEntity?,
    onDismiss: () -> Unit,
    onRetry: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isRetrying by remember { mutableStateOf(false) }

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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = ObsidianDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "Payload Inspector (v1.1)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = event.eventId,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Deduplication & Verification Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EVENT CLASSIFICATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            StatusBadge(status = event.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Idempotency Key: ${event.idempotencyKey.ifBlank { "${event.provider}:${event.eventId}" }}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Layer A Duplicate: ${if (event.isMessageDuplicate) "YES" else "No"}",
                                fontSize = 11.sp,
                                color = if (event.isMessageDuplicate) ErrorRed else TextSecondary,
                                fontWeight = if (event.isMessageDuplicate) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Layer B Duplicate: ${if (event.isTransactionDuplicate) "YES" else "No"}",
                                fontSize = 11.sp,
                                color = if (event.isTransactionDuplicate) ErrorRed else TextSecondary,
                                fontWeight = if (event.isTransactionDuplicate) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (!event.reviewReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianBlack)
                                    .border(1.dp, ObsidianBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Reason: ${event.reviewReason}",
                                    fontSize = 10.sp,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Normalized JSON Payload View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VERSIONED 1.1 JSON PAYLOAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Wisfy Payload", jsonString))
                            Toast.makeText(context, "Payload copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = jsonString,
                        color = Color(0xFFC9D1D9),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }

                // If error exists on log
                if (log != null && !log.errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ErrorContainer)
                            .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "DELIVERY ERROR EXCERPT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = log.errorMessage,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Retry & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (log != null && log.status == "failed" && onRetry != null) {
                        Button(
                            onClick = {
                                isRetrying = true
                                onRetry(log.id)
                            },
                            enabled = !isRetrying,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("retry_delivery_button"),
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = ObsidianBlack
                            )
                        ) {
                            if (isRetrying) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianBlack, strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp), tint = ObsidianBlack)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Delivery", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ObsidianCardHighlight,
                            contentColor = TextPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ObsidianBorder)
                        )
                    ) {
                        Text("Close", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
