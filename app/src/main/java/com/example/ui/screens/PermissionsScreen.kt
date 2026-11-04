package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.WisfyCtaButton
import com.example.ui.components.WisfyDeltaChip
import com.example.ui.components.WisfyIconTile
import com.example.ui.components.WisfyLayeredCard
import com.example.ui.components.WisfyThinProgress
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.WisfySpacing

@Composable
fun PermissionsScreen(
    onPermissionsGrantedOrSkipped: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val colors = LocalWisfyColors.current

    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.RECEIVE_SMS] == true
        hasSmsPermission = smsGranted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        }
        if (smsGranted) {
            onPermissionsGrantedOrSkipped()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = WisfySpacing.screenPadding, vertical = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicator & Stepper Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step 2 of 2",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                Text(
                    text = "Device Access",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.emerald
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thin Stepper Progress Bar
            WisfyThinProgress(
                progress = 1.0f,
                fillColor = colors.emerald
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title Header
            Text(
                text = "Required Access",
                fontFamily = InterFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Wisfy runs strictly on-device. These permissions allow the local engine to parse messages and forward webhooks.",
                fontFamily = InterFontFamily,
                fontSize = 13.5.sp,
                color = colors.textSecondary,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Permission Card 1: SMS Ingestion
            PermissionLayeredCard(
                icon = Icons.Default.Sms,
                title = "Receive & Read SMS",
                reason = "Allows the background service to capture payment and OTP messages in real-time.",
                isGranted = hasSmsPermission
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Permission Card 2: Notifications
            PermissionLayeredCard(
                icon = Icons.Default.Notifications,
                title = "Foreground Notification",
                reason = "Keeps the local relay alive when running in the background.",
                isGranted = hasNotificationPermission
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Permission Card 3: Battery Optimization
            PermissionLayeredCard(
                icon = Icons.Default.BatterySaver,
                title = "Background Continuity",
                reason = "Ensures Android does not put the SMS listener to sleep during idle state.",
                isGranted = true
            )
        }

        // Bottom CTA Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 12.dp)
        ) {
            WisfyCtaButton(
                text = if (hasSmsPermission) "Continue to Dashboard" else "Grant Access",
                onClick = {
                    if (hasSmsPermission) {
                        onPermissionsGrantedOrSkipped()
                    } else {
                        val toRequest = mutableListOf(
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            toRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionLauncher.launch(toRequest.toTypedArray())
                    }
                },
                testTag = "grant_permissions_btn"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Skip for now",
                fontFamily = InterFontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPermissionsGrantedOrSkipped() }
                    .padding(vertical = 8.dp)
                    .testTag("skip_permissions_btn")
            )
        }
    }
}

@Composable
private fun PermissionLayeredCard(
    icon: ImageVector,
    title: String,
    reason: String,
    isGranted: Boolean
) {
    val colors = LocalWisfyColors.current

    WisfyLayeredCard(
        icon = icon,
        iconTint = if (isGranted) colors.emerald else colors.statusPending,
        title = title
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reason,
                fontFamily = InterFontFamily,
                fontSize = 12.5.sp,
                color = colors.textSecondary,
                lineHeight = 17.sp,
                modifier = Modifier.weight(1f).padding(end = 10.dp)
            )

            WisfyDeltaChip(
                text = if (isGranted) "Granted" else "Required",
                isPositive = isGranted,
                leadingArrow = false
            )
        }
    }
}
