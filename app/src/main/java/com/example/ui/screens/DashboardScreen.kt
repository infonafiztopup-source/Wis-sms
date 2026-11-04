package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Webhook
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.ui.components.WisfyCtaButton
import com.example.ui.components.WisfyDeltaChip
import com.example.ui.components.WisfyEmptyState
import com.example.ui.components.WisfyHeroCard
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.components.WisfyPillBars
import com.example.ui.components.WisfyThinProgress
import com.example.ui.components.WisfyTransactionRow
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfySpacing
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
    val colors = LocalWisfyColors.current
    var selectedFilter by remember { mutableStateOf("All") }

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

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.US) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = WisfySpacing.screenPadding)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Hero Card (Graphite Gradient with Metallic Circular Buttons)
            WisfyHeroCard(
                pillText = if (isServiceActive) "DEVICE • **** 6780" else "DAEMON • PAUSED",
                label = "Delivered Today",
                mainValue = "$deliveredCount",
                subValue = if (totalCount > 0) " / $totalCount" else null,
                onSimulateClick = onSimulateSmsClick,
                onAddEndpointClick = onAddEndpointClick,
                onProvidersClick = onOpenProviderProfilesClick,
                onDocsClick = onOpenDocsClick,
                isServiceActive = isServiceActive
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Section: Your Endpoints + "View all →"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your endpoints",
                    fontFamily = InterFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "${endpoints.size} active",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (endpoints.isEmpty()) {
                WisfyLayeredCard(
                    icon = Icons.Default.Webhook,
                    title = "No Active Endpoints",
                    onChevronClick = onAddEndpointClick
                ) {
                    Text(
                        text = "Connect an HTTPS webhook destination to automatically forward all incoming SMS payloads.",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    WisfyCtaButton(
                        text = "+ Connect endpoint",
                        onClick = onAddEndpointClick,
                        modifier = Modifier.height(44.dp)
                    )
                }
            } else {
                endpoints.take(2).forEach { endpoint ->
                    val epDelivered = logs.count { it.endpointId == endpoint.id && it.status == "delivered" }
                    val epTotal = logs.count { it.endpointId == endpoint.id }
                    val progressFraction = if (epTotal > 0) epDelivered.toFloat() / epTotal.toFloat() else 1f

                    WisfyLayeredCard(
                        icon = Icons.Default.Webhook,
                        iconTint = colors.emerald,
                        title = endpoint.name,
                        onChevronClick = onOpenDocsClick,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$epDelivered delivered",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "$epTotal total",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4dp fully rounded progress bar
                        WisfyThinProgress(
                            progress = progressFraction,
                            fillColor = colors.emerald
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "avg 140 ms",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.5.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.emerald
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Section: Dispatch Analytics (Pill Bars Chart with Diagonal Hatch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Ingestion",
                    fontFamily = InterFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.3).sp
                )
                WisfyDeltaChip(
                    text = successRate,
                    isPositive = true,
                    leadingArrow = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            WisfyLayeredCard(
                title = "Pipeline Throughput"
            ) {
                WisfyPillBars(
                    values = listOf(0.45f, 0.70f, 0.35f, 0.90f, 0.60f, 0.85f, 0.75f),
                    labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                    barHeight = 90.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Section: Recent Activity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent activity",
                    fontFamily = InterFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "${filteredLogs.size} logs",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Feed Items
        if (filteredLogs.isEmpty()) {
            item {
                WisfyLayeredCard(
                    icon = Icons.Default.Sms,
                    title = "Waiting for your first SMS"
                ) {
                    Text(
                        text = "Trigger a real SMS or use the metallic Simulate button above to generate a sandbox event.",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    WisfyCtaButton(
                        text = "+ Simulate an SMS",
                        onClick = onSimulateSmsClick,
                        modifier = Modifier.height(44.dp)
                    )
                }
            }
        } else {
            items(filteredLogs.take(5), key = { it.id }) { log ->
                val relatedEvent = events.find { it.eventId == log.eventId }
                val title = relatedEvent?.sender ?: "Webhook Log"
                val subtitle = if (!relatedEvent?.transactionId.isNullOrBlank()) {
                    "TrxID: ${relatedEvent?.transactionId}"
                } else {
                    log.endpointUrl
                }
                val amount = if (relatedEvent?.amount != null) {
                    "৳ ${relatedEvent.amount}"
                } else {
                    "Event #${log.id}"
                }
                val timeStr = timeFormat.format(Date(log.createdAt))

                val statusColor = when (log.status) {
                    "delivered" -> colors.emerald
                    "queued", "sending" -> colors.statusPending
                    else -> colors.statusFailed
                }

                WisfyTransactionRow(
                    title = title,
                    subtitle = subtitle,
                    amount = amount,
                    time = timeStr,
                    statusColor = statusColor,
                    onClick = {
                        if (relatedEvent != null) {
                            onInspectEvent(relatedEvent, log)
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Bottom space reserved so no content is hidden beneath floating nav
        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
