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
import com.example.ui.components.ButtonVariant
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.components.WisfyEmptyState
import com.example.ui.dialogs.WebhookTestResultDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and Connect Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = "Webhook Endpoints",
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.4).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${endpoints.size} configured destinations",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }

            WisfyButton(
                text = "Connect",
                icon = Icons.Default.Add,
                onClick = onAddEndpointClick,
                variant = ButtonVariant.PRIMARY,
                height = 40.dp,
                testTag = "endpoints_add_button"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (endpoints.isEmpty()) {
            WisfyEmptyState(
                icon = Icons.Default.Webhook,
                title = "No Webhooks Configured",
                subtitle = "Add your own HTTPS endpoint to forward incoming SMS events directly to your server or merchant gateway.",
                actionText = "Connect Webhook",
                onActionClick = onAddEndpointClick,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(endpoints, key = { it.id }) { endpoint ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { 20 }
                    ) {
                        EndpointCard(
                            endpoint = endpoint,
                            onToggle = { onToggleEndpoint(endpoint) },
                            onEdit = { onEditEndpointClick(endpoint) },
                            onDelete = { onDeleteEndpointClick(endpoint.id) },
                            onTest = { callback ->
                                onTestEndpoint(endpoint) { result ->
                                    callback(result)
                                    inspectedTestResult = Pair(endpoint, result)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
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
private fun EndpointCard(
    endpoint: WebhookEndpointEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: ((DispatchResult) -> Unit) -> Unit
) {
    var isTesting by remember { mutableStateOf(false) }
    val colors = LocalWisfyColors.current
    val context = LocalContext.current

    WisfyCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Endpoint Name, Badges & Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = endpoint.name,
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Auth Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (endpoint.authToken.isNotBlank()) colors.statusSuccessBg else colors.surfaceLow)
                                .border(
                                    1.dp,
                                    if (endpoint.authToken.isNotBlank()) colors.statusSuccess.copy(alpha = 0.3f) else colors.borderSubtle,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (endpoint.authToken.isNotBlank()) "BEARER AUTH" else "NO AUTH",
                                fontSize = 9.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (endpoint.authToken.isNotBlank()) colors.statusSuccess else colors.textMuted
                            )
                        }

                        // SIM Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceLow)
                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SimCard,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = when (endpoint.simSlot) {
                                        1 -> "SIM 1"
                                        2 -> "SIM 2"
                                        else -> "BOTH SIMS"
                                    },
                                    fontSize = 9.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }

                Switch(
                    checked = endpoint.enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldGreen,
                        uncheckedThumbColor = colors.textSecondary,
                        uncheckedTrackColor = colors.surfaceLow
                    ),
                    modifier = Modifier.testTag("toggle_endpoint_${endpoint.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // URL Box with Copy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceLow)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = endpoint.url,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMonoFontFamily,
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
                        .size(14.dp)
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Webhook URL", endpoint.url))
                            Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                        }
                )
            }

            // Sender whitelist chip if configured
            if (endpoint.senderFilter.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Filter: ",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Text(
                        text = endpoint.senderFilter,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = EmeraldDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Last test outcome
            if (endpoint.lastTestedAt != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (endpoint.lastTestSuccess == true) colors.statusSuccessBg else colors.statusErrorBg)
                        .border(
                            1.dp,
                            if (endpoint.lastTestSuccess == true) colors.statusSuccess.copy(alpha = 0.3f) else colors.statusError.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (endpoint.lastTestSuccess == true) EmeraldGreen else ErrorRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Last Ping: " + (endpoint.lastTestMessage ?: if (endpoint.lastTestSuccess == true) "Success" else "Failed"),
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (endpoint.lastTestSuccess == true) colors.statusSuccess else colors.statusError,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action row: Test Webhook, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WisfyButton(
                    text = if (isTesting) "Testing..." else "Test Webhook",
                    icon = if (isTesting) null else Icons.Default.NetworkPing,
                    onClick = {
                        isTesting = true
                        onTest {
                            isTesting = false
                        }
                    },
                    variant = ButtonVariant.OUTLINE,
                    loading = isTesting,
                    height = 36.dp,
                    testTag = "test_endpoint_btn_${endpoint.id}"
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
