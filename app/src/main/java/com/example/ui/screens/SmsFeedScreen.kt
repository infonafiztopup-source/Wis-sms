package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.ui.components.WisfyEmptyState
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.components.WisfyTransactionRow
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfyShapes
import com.example.ui.theme.WisfySpacing
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
    val colors = LocalWisfyColors.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

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

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.US) }
    val isDark = colors.isDark

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = WisfySpacing.screenPadding)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SMS Feed",
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        letterSpacing = (-0.4).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${events.size} captured events recorded",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search pill with round filter button (Salung / Reference 4 style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search by sender, TrxID...",
                            fontFamily = InterFontFamily,
                            fontSize = 13.5.sp,
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
                        .weight(1f)
                        .testTag("sms_feed_search_input"),
                    shape = WisfyShapes.Pill,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceCard,
                        unfocusedContainerColor = colors.surfaceCard,
                        focusedBorderColor = colors.buttonPrimary,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    singleLine = true
                )

                // Round Filter Toggle Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceCard)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable { selectedFilter = "All" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter Chips (Black in Light / White in Dark)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Delivered", "Pending", "Failed").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val pillBg = if (isSelected) {
                        if (isDark) Color(0xFFFAFAFA) else Color(0xFF1A1A1C)
                    } else colors.surfaceCard

                    val pillText = if (isSelected) {
                        if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
                    } else colors.textSecondary

                    val pillBorder = if (isSelected) {
                        if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1C)
                    } else colors.border

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(WisfyShapes.Pill)
                            .background(pillBg)
                            .border(1.dp, pillBorder, WisfyShapes.Pill)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = pillText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Events List (Grouped into clean white transaction cards)
        if (filteredEvents.isEmpty()) {
            item {
                WisfyLayeredCard(
                    icon = Icons.Default.Sms,
                    title = "No SMS Events Found"
                ) {
                    WisfyEmptyState(
                        icon = Icons.Default.Sms,
                        title = "No events match this filter",
                        subtitle = "Try selecting 'All' or trigger a simulated event."
                    )
                }
            }
        } else {
            items(filteredEvents, key = { it.eventId }) { event ->
                val relatedLog = logs.find { it.eventId == event.eventId }
                val timeStr = timeFormat.format(Date(event.receivedAt))

                val statusColor = when (relatedLog?.status ?: event.status) {
                    "delivered" -> colors.emerald
                    "queued", "sending" -> colors.statusPending
                    else -> colors.statusFailed
                }

                val amountStr = if (event.amount != null) {
                    "৳ ${event.amount}"
                } else {
                    "1 msg"
                }

                val subText = if (!event.transactionId.isNullOrBlank()) {
                    "TrxID: ${event.transactionId}"
                } else {
                    event.rawBody.take(28) + "..."
                }

                WisfyTransactionRow(
                    title = event.sender,
                    subtitle = subText,
                    amount = amountStr,
                    time = timeStr,
                    statusColor = statusColor,
                    onClick = { onInspectPayload(event, relatedLog) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Bottom space so no rows are hidden under nav
        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
