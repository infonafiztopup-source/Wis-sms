package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.network.DispatchResult
import com.example.ui.components.WisfyCtaButton
import com.example.ui.components.WisfyDeltaChip
import com.example.ui.components.WisfyEmptyState
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.dialogs.WebhookTestResultDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfyShapes
import com.example.ui.theme.WisfySpacing

@Composable
fun EndpointsScreen(
    endpoints: List<WebhookEndpointEntity>,
    onAddEndpointClick: () -> Unit,
    onEditEndpointClick: (WebhookEndpointEntity) -> Unit,
    onDeleteEndpointClick: (Long) -> Unit,
    onToggleEndpoint: (WebhookEndpointEntity) -> Unit,
    onTestEndpoint: (WebhookEndpointEntity, (DispatchResult) -> Unit) -> Unit
) {
    var inspectedTestResult by remember { mutableStateOf<Pair<WebhookEndpointEntity, DispatchResult>?>(null) }
    val colors = LocalWisfyColors.current
    val activeCount = endpoints.count { it.enabled }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = WisfySpacing.screenPadding)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Title and Active Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Webhook Endpoints",
                            fontFamily = InterFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            letterSpacing = (-0.4).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$activeCount active · ${endpoints.size} configured",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (endpoints.isEmpty()) {
                item {
                    WisfyLayeredCard(
                        icon = Icons.Default.Webhook,
                        title = "No Webhooks Configured",
                        onChevronClick = onAddEndpointClick
                    ) {
                        WisfyEmptyState(
                            icon = Icons.Default.Webhook,
                            title = "No Destinations Found",
                            subtitle = "Connect an HTTPS endpoint to forward incoming SMS messages to your server in real-time."
                        )
                    }
                }
            } else {
                items(endpoints, key = { it.id }) { endpoint ->
                    EndpointLayeredCard(
                        endpoint = endpoint,
                        onToggle = { onToggleEndpoint(endpoint) },
                        onEdit = { onEditEndpointClick(endpoint) },
                        onDelete = { onDeleteEndpointClick(endpoint.id) },
                        onTest = { callback ->
                            onTestEndpoint(endpoint) { result ->
                                callback(result)
                                inspectedTestResult = Pair(endpoint, result)
                            }
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // Bottom space for floating CTA + black nav
            item {
                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        // Bottom Floating Dark CTA Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 84.dp)
        ) {
            WisfyCtaButton(
                text = "+ Add endpoint",
                onClick = onAddEndpointClick,
                testTag = "endpoints_add_button"
            )
        }
    }

    inspectedTestResult?.let { (ep, res) ->
        WebhookTestResultDialog(
            endpointName = ep.name,
            endpointUrl = ep.url,
            result = res,
            onDismiss = { inspectedTestResult = null }
        )
    }
}

@Composable
private fun EndpointLayeredCard(
    endpoint: WebhookEndpointEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: ((DispatchResult) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWisfyColors.current
    val context = LocalContext.current
    var isTesting by remember { mutableStateOf(false) }

    WisfyLayeredCard(
        icon = Icons.Default.Webhook,
        iconTint = if (endpoint.enabled) colors.emerald else colors.textMuted,
        title = endpoint.name,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // URL snippet box with copy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WisfyShapes.ChevronButton)
                    .background(colors.surfaceHighlight)
                    .border(1.dp, colors.border, WisfyShapes.ChevronButton)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = endpoint.url,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                )

                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy URL",
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", endpoint.url))
                            Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                        }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Badges (Auth, SIM, Filter)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auth badge
                Box(
                    modifier = Modifier
                        .clip(WisfyShapes.Pill)
                        .background(if (endpoint.authToken.isNotBlank()) colors.emeraldLight else colors.surfaceHighlight)
                        .border(1.dp, if (endpoint.authToken.isNotBlank()) colors.emerald.copy(alpha = 0.3f) else colors.border, WisfyShapes.Pill)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (endpoint.authToken.isNotBlank()) "BEARER AUTH" else "NO AUTH",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (endpoint.authToken.isNotBlank()) colors.emeraldDark else colors.textMuted
                    )
                }

                // SIM badge
                Box(
                    modifier = Modifier
                        .clip(WisfyShapes.Pill)
                        .background(colors.surfaceHighlight)
                        .border(1.dp, colors.border, WisfyShapes.Pill)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SimCard,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = when (endpoint.simSlot) {
                                1 -> "SIM 1"
                                2 -> "SIM 2"
                                else -> "BOTH SIMS"
                            },
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            if (endpoint.senderFilter.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Filter: ${endpoint.senderFilter}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = colors.emeraldDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Enable Switch + Test Webhook + Edit + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Switch
                Switch(
                    checked = endpoint.enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF111113),
                        uncheckedThumbColor = colors.textSecondary,
                        uncheckedTrackColor = colors.surfaceHighlight
                    ),
                    modifier = Modifier.testTag("toggle_endpoint_${endpoint.id}")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Test Button (Ghost Pill)
                    Box(
                        modifier = Modifier
                            .clip(WisfyShapes.Pill)
                            .background(colors.surfaceHighlight)
                            .border(1.dp, colors.border, WisfyShapes.Pill)
                            .clickable(enabled = !isTesting) {
                                isTesting = true
                                onTest { isTesting = false }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("test_endpoint_btn_${endpoint.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 1.5.dp,
                                color = colors.textPrimary
                            )
                        } else {
                            Text(
                                text = "Test Ping",
                                fontFamily = InterFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ErrorRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
