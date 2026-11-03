package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.BrandTag
import com.example.ui.components.ButtonVariant
import com.example.ui.components.WisfyBrandShape
import com.example.ui.components.WisfyButton
import com.example.ui.components.WisfyCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors

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

    var consentChecked by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.RECEIVE_SMS] == true
        hasSmsPermission = smsGranted
        if (smsGranted) {
            onPermissionsGrantedOrSkipped()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvasBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandTag(text = "STEP 2 OF 2 • DEVICE PRIVACY")
                WisfyBrandShape(modifier = Modifier.size(28.dp), contentDescription = "Wisfy Logo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Title Header
            Text(
                text = "Permissions & Privacy",
                fontFamily = InterFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Wisfy operates exclusively on-device. Review the background dispatch permissions required to relay payment SMS.",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = colors.textSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Grouped Permissions Card
            WisfyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PermissionRowItem(
                        icon = Icons.Default.Sms,
                        iconColor = EmeraldGreen,
                        iconBg = colors.statusSuccessBg,
                        title = "SMS Event Listener",
                        badgeText = if (hasSmsPermission) "GRANTED" else "REQUIRED",
                        badgeIsGranted = hasSmsPermission,
                        description = "Directly monitors incoming transactional SMS broadcasts and converts matching payloads into webhook events."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.borderSubtle)
                    )

                    PermissionRowItem(
                        icon = Icons.Default.BatterySaver,
                        iconColor = Color(0xFFD97706),
                        iconBg = if (colors.isDark) Color(0xFF1E293B) else Color(0xFFFEF3C7),
                        title = "Battery Optimization Exemption",
                        badgeText = "RECOMMENDED",
                        badgeIsGranted = false,
                        description = "Prevents Android from putting the background listener daemon to sleep during critical payment windows."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.borderSubtle)
                    )

                    PermissionRowItem(
                        icon = Icons.Default.Shield,
                        iconColor = Color(0xFF2563EB),
                        iconBg = if (colors.isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                        title = "Zero Cloud Intermediary",
                        badgeText = "VERIFIED",
                        badgeIsGranted = true,
                        description = "Your sensitive SMS data never touches intermediate proxy clouds. Direct HTTPS dispatch straight to your server."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Notice Card
            WisfyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(colors.statusSuccessBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Every dispatched SMS is inspectable in your local SMS Feed. Cryptographic endpoint keys are stored securely in Android KeyStore & SQLite.",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Authorization Consent Row
            WisfyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                onClick = { consentChecked = !consentChecked }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Checkbox(
                        checked = consentChecked,
                        onCheckedChange = { consentChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EmeraldGreen,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("consent_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I authorize local SMS forwarding to configured webhooks",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Action buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            WisfyButton(
                text = if (hasSmsPermission) "Access Granted • Continue" else "Allow SMS Permissions",
                icon = if (hasSmsPermission) Icons.Default.CheckCircle else Icons.Default.Lock,
                enabled = consentChecked,
                onClick = {
                    if (consentChecked) {
                        val permissionsToRequest = mutableListOf(
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionLauncher.launch(permissionsToRequest.toTypedArray())
                    }
                },
                variant = ButtonVariant.PRIMARY,
                height = 46.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grant_permissions_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary option for simulation & local testing
            WisfyButton(
                text = "Proceed to Dashboard (Simulation Mode)",
                variant = ButtonVariant.SECONDARY,
                height = 44.dp,
                onClick = onPermissionsGrantedOrSkipped,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("skip_permissions_button")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Encrypted • Strict Local Execution",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun PermissionRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    badgeText: String,
    badgeIsGranted: Boolean,
    description: String
) {
    val colors = LocalWisfyColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg)
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(
                            if (badgeIsGranted) colors.statusSuccessBg else colors.surfaceLow
                        )
                        .border(
                            1.dp,
                            if (badgeIsGranted) colors.statusSuccess.copy(alpha = 0.3f) else colors.borderSubtle,
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (badgeIsGranted) colors.statusSuccess else colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                color = colors.textSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
