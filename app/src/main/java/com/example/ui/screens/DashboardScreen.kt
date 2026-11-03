package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.ButtonVariant
import com.example.ui.components.PulsingStatusDot
import com.example.ui.components.StatusBadge
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.components.WisfyEmptyState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    endpoints: List<WebhookEndpointEntity>,
    events: List<SmsEventEntity>,
    logs: List<DeliveryLogEntity>,
    deliveredCount: Int,
    queuedCount: Int,
    failedCount: Int,
    totalCount: Int,
    isServiceActive: Boolean,
    onAddEndpointClick: () -> Unit,
    onSimulateSmsClick: () -> Unit,
    onOpenDocsClick: () -> Unit,
    onOpenProviderProfilesClick: () -> Unit,
    onInspectEvent: (SmsEventEntity, DeliveryLogEntity?) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val colors = LocalWisfyColors.current

    val filteredLogs = remember(logs, selectedFilter) {
        when (selectedFilter) {
            "Delivered" -> logs.filter { it.status == "delivered" }
            "Pending" -> logs.filter { it.status in listOf("queued", "sending", "retry_scheduled") }
            "Failed" -> logs.filter { it.status == "failed" }
            else -> logs
        }
    }

    val successRate = remember(deliveredCount, totalCount) {
        if (totalCount == 0) "100%"
        else {
            val rate = (deliveredCount.toDouble() / totalCount.toDouble()) * 100.0
            String.format(Locale.US, "%.1f%%", rate)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // Dashboard Header & Live Telemetry Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Telemetry & Operations",
                        fontFamily = InterFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = (-0.4).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time SMS ingestion & webhook status",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                // Live Service Activity Beacon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isServiceActive) colors.statusSuccessBg else colors.surfaceElevated)
                        .border(
                            1.dp,
                            if (isServiceActive) colors.statusSuccess.copy(alpha = 0.35f) else colors.borderSubtle,
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    PulsingStatusDot(
                        color = if (isServiceActive) EmeraldGreen else Color(0xFF94A3B8),
                        size = 7.dp,
                        pulseScale = if (isServiceActive) 1.8f else 1.0f
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isServiceActive) "ACTIVE" else "STANDBY",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (isServiceActive) colors.statusSuccess else colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linear-Style Deduplication Trust Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2-Layer Deduplication Engine Active",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "L1-TrxID • L2-Sig",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = EmeraldDark,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Grid Metric Cards with Animated Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Delivered",
                    numericValue = deliveredCount,
                    subText = "Webhooks received",
                    icon = Icons.Default.CheckCircle,
                    iconTint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "In Queue",
                    numericValue = queuedCount,
                    subText = "Pending dispatch",
                    icon = Icons.Default.Schedule,
                    iconTint = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Failed",
                    numericValue = failedCount,
                    subText = if (failedCount == 0) "0 Errors" else "Requires retry",
                    icon = Icons.Default.Warning,
                    iconTint = if (failedCount > 0) ErrorRed else EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Success Rate",
                    staticValue = successRate,
                    subText = "${endpoints.size} active endpoints",
                    icon = Icons.Default.Hub,
                    iconTint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pipeline Command Center Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PIPELINE ENGINE",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // MFS Profiles Action Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(colors.surfaceElevated)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(9999.dp))
                                    .clickable(onClick = onOpenProviderProfilesClick)
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = EmeraldGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "MFS Profiles",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }

                            // Receiver Docs Action Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(colors.surfaceElevated)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(9999.dp))
                                    .clickable(onClick = onOpenDocsClick)
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.IntegrationInstructions,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = EmeraldGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Docs",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Financial Capture & Dispatch Hub",
                        fontFamily = InterFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        letterSpacing = (-0.4).sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "$deliveredCount delivered • $queuedCount queued • Strict Schema v1.1",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tactile Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WisfyButton(
                            text = "Add Webhook",
                            icon = Icons.Default.Add,
                            onClick = onAddEndpointClick,
                            variant = ButtonVariant.PRIMARY,
                            height = 42.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_add_webhook_btn")
                        )

                        WisfyButton(
                            text = "Simulate SMS",
                            icon = Icons.Default.PlayArrow,
                            onClick = onSimulateSmsClick,
                            variant = ButtonVariant.SECONDARY,
                            height = 42.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_simulate_sms_btn")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Controls for Live Feed
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
                        text = "Live Dispatched Feed",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredLogs.size} events",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }

                // Filter Pill selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                        .padding(3.dp)
                ) {
                    listOf("All", "Delivered", "Pending").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colors.statusSuccessBg else Color.Transparent)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
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
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Feed Items List
        if (filteredLogs.isEmpty()) {
            item {
                WisfyEmptyState(
                    icon = Icons.Default.Sms,
                    title = "No Dispatched Events Yet",
                    subtitle = "Configure an endpoint and tap 'Simulate SMS' to test real-time SMS webhook forwarding.",
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                val associatedEvent = events.find { it.eventId == log.eventId }
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { 20 }
                ) {
                    FeedCard(
                        log = log,
                        event = associatedEvent,
                        onClick = {
                            if (associatedEvent != null) {
                                onInspectEvent(associatedEvent, log)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    numericValue: Int? = null,
    staticValue: String? = null,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalWisfyColors.current

    WisfyCard(
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (numericValue != null) {
                AnimatedCounterText(
                    count = numericValue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            } else {
                Text(
                    text = staticValue ?: "",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.5).sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subText,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FeedCard(
    log: DeliveryLogEntity,
    event: SmsEventEntity?,
    onClick: () -> Unit
) {
    val colors = LocalWisfyColors.current
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }
    val formattedTime = remember(log.createdAt) { timeFormat.format(Date(log.createdAt)) }

    WisfyCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceLow)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = event?.eventId?.takeLast(8)?.uppercase() ?: "EVENT #${log.id}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily,
                            color = EmeraldDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = log.status)
                }

                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = colors.textMuted,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Details Monospace Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceLow)
                    .border(1.dp, colors.borderSubtle.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ENDPOINT: ${log.endpointName}",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                        )
                        Text(
                            text = if (event?.amount != null) "payment.candidate" else "sms.received",
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            color = EmeraldDark,
                            maxLines = 1
                        )
                    }

                    if (event?.amount != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Candidate Amount: ${event.currency ?: "Tk"} ${event.amount}",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                            )
                            if (!event.transactionId.isNullOrBlank()) {
                                Text(
                                    text = "Trx: ${event.transactionId}",
                                    fontSize = 11.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "From ${event?.sender ?: "Unknown"}: ${event?.sanitizedBody ?: log.responseBodySnippet ?: "Forwarded"}",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
