package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.ui.components.ButtonVariant
import com.example.ui.components.StatusBadge
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.components.WisfyEmptyState
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmsFeedScreen(
    events: List<SmsEventEntity>,
    logs: List<DeliveryLogEntity>,
    onInspectPayload: (SmsEventEntity, DeliveryLogEntity?) -> Unit,
    onRetryLog: (Long) -> Unit,
    onResendEvent: (String, () -> Unit) -> Unit = { _, cb -> cb() }
) {
    val context = LocalContext.current
    val colors = LocalWisfyColors.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var resendingEventId by remember { mutableStateOf<String?>(null) }

    val filteredEvents = remember(events, logs, searchQuery, selectedFilter) {
        events.filter { event ->
            val matchesSearch = searchQuery.isBlank() ||
                    event.sender.contains(searchQuery, ignoreCase = true) ||
                    (event.transactionId ?: "").contains(searchQuery, ignoreCase = true) ||
                    event.eventId.contains(searchQuery, ignoreCase = true)

            val relatedLogs = logs.filter { it.eventId == event.eventId }
            val matchesFilter = when (selectedFilter) {
                "Delivered" -> relatedLogs.any { it.status == "delivered" }
                "Pending" -> relatedLogs.any { it.status in listOf("queued", "sending", "retry_scheduled") } || event.status == "queued"
                "Failed" -> relatedLogs.any { it.status == "failed" } || event.status == "failed"
                "Filtered" -> event.status.startsWith("filtered")
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val timeFormat = remember { SimpleDateFormat("dd MMM, HH:mm:ss", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SMS Feed & Events",
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.4).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${events.size} captured events recorded",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Linear-Style Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search by sender, TrxID, or Event ID...",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = colors.textMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colors.textMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sms_feed_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceElevated,
                unfocusedContainerColor = colors.surfaceElevated,
                focusedBorderColor = EmeraldGreen,
                unfocusedBorderColor = colors.borderSubtle,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Delivered", "Pending", "Failed", "Filtered").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isSelected) colors.statusSuccessBg else colors.surfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) colors.statusSuccess.copy(alpha = 0.4f) else colors.borderSubtle,
                            RoundedCornerShape(9999.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 11.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) colors.statusSuccess else colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Events List
        if (filteredEvents.isEmpty()) {
            WisfyEmptyState(
                icon = Icons.Default.Sms,
                title = "No SMS Events Found",
                subtitle = "Try clearing filters or simulate an incoming SMS event from the top navigation.",
                modifier = Modifier.padding(top = 28.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredEvents, key = { it.eventId }) { event ->
                    val relatedLogs = logs.filter { it.eventId == event.eventId }
                    val primaryLog = relatedLogs.firstOrNull()
                    val hasFailedOrPending = relatedLogs.any { it.status in listOf("failed", "queued", "sending", "retry_scheduled") } ||
                            event.status in listOf("failed", "queued")

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { 20 }
                    ) {
                        WisfyCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Top Row: Sender, SIM Slot, Provider Badge + Time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = event.sender,
                                            fontFamily = InterFontFamily,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(colors.surfaceLow)
                                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = event.provider.uppercase(),
                                                fontSize = 9.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldDark,
                                                maxLines = 1
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))
                                        // SIM Slot Tag
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(colors.surfaceLow)
                                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (event.simSlot == 2) "SIM 2" else "SIM 1",
                                                fontSize = 9.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textMuted
                                            )
                                        }
                                    }

                                    Text(
                                        text = timeFormat.format(Date(event.receivedAt)),
                                        fontSize = 11.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = colors.textMuted,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Parsed details badge (if payment detected)
                                if (event.amount != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.statusSuccessBg)
                                            .border(
                                                1.dp,
                                                colors.statusSuccess.copy(alpha = 0.3f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Detected: ${event.currency ?: "Tk"} ${event.amount}",
                                                fontFamily = InterFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.statusSuccess,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f).padding(end = 6.dp)
                                            )
                                            if (!event.transactionId.isNullOrBlank()) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText("TrxID", event.transactionId))
                                                        Toast.makeText(context, "TrxID copied: ${event.transactionId}", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Text(
                                                        text = "Trx: ${event.transactionId}",
                                                        fontSize = 11.sp,
                                                        fontFamily = JetBrainsMonoFontFamily,
                                                        color = colors.statusSuccess,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copy TrxID",
                                                        tint = colors.statusSuccess,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Sanitized Body
                                Text(
                                    text = event.sanitizedBody,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Deliveries list for this event
                                if (relatedLogs.isNotEmpty()) {
                                    relatedLogs.forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "-> ${log.endpointName}",
                                                fontFamily = InterFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = colors.textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            )
                                            StatusBadge(status = log.status)
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "No endpoints matched filter",
                                            fontFamily = InterFontFamily,
                                            fontSize = 11.sp,
                                            color = colors.textMuted
                                        )
                                        StatusBadge(status = event.status)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action buttons: Inspect Payload & Resend / Retry
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    WisfyButton(
                                        text = "Inspect Payload",
                                        icon = Icons.Default.Code,
                                        onClick = { onInspectPayload(event, primaryLog) },
                                        variant = ButtonVariant.SECONDARY,
                                        height = 36.dp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("inspect_payload_btn_${event.eventId}")
                                    )

                                    // Resend / Retry Button
                                    val isResendingThis = resendingEventId == event.eventId
                                    WisfyButton(
                                        text = if (hasFailedOrPending) "Retry Dispatch" else "Resend",
                                        icon = if (isResendingThis) null else Icons.Default.Replay,
                                        onClick = {
                                            resendingEventId = event.eventId
                                            onResendEvent(event.eventId) {
                                                resendingEventId = null
                                                Toast.makeText(context, "Dispatched retry for event", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        enabled = !isResendingThis,
                                        loading = isResendingThis,
                                        variant = if (hasFailedOrPending) ButtonVariant.DESTRUCTIVE else ButtonVariant.PRIMARY,
                                        height = 36.dp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("resend_event_btn_${event.eventId}")
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}
