package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.parser.ParserRegistry
import com.example.domain.parser.ProviderProfiles
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.WarmSurfaceCard

@Composable
fun ProviderProfilesDialog(
    onDismiss: () -> Unit
) {
    var selectedProfileId by remember { mutableStateOf("bkash") }
    val scrollState = rememberScrollState()

    val currentProfile = remember(selectedProfileId) {
        ProviderProfiles.allProfiles.find { it.providerId == selectedProfileId } ?: ProviderProfiles.bKash
    }

    // Sample templates for preview
    val sampleTemplates = remember(selectedProfileId) {
        when (selectedProfileId) {
            "bkash" -> listOf(
                Pair("Valid Incoming Payment", "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30"),
                Pair("Outgoing Cash-out (Filtered)", "Cash Out Tk 500.00 to 01712345678. Fee Tk 7.50. Balance Tk 14,733.00. TrxID 9K8A7L6M2Q"),
                Pair("Fake / Spoofed Sender Test", "You have received Tk 5,000.00 from 01999999999. TrxID FAKE1234")
            )
            "nagad" -> listOf(
                Pair("Valid Incoming Payment", "Money received. Amount: Tk 1,200.00 from 01987654321. TxnID: 7X89QPL2. Balance: Tk 6,400.00"),
                Pair("Outgoing Send Money (Filtered)", "Send Money Tk 400.00 to 01987654321. TxnID: 7X89QPL3. Fee: Tk 5.00"),
                Pair("Template Mismatch", "Welcome to Nagad. Recharge your mobile with exciting cashback offers.")
            )
            "rocket" -> listOf(
                Pair("Valid Incoming Payment", "Tk 3,000.00 received from 01812345678. TxnId: 8A99LKJ1. Balance Tk 8,500.00"),
                Pair("Reversal Alert (Filtered)", "Transaction reversed. Tk 500.00 returned. TxnId: 8A99LKJ2")
            )
            else -> listOf(
                Pair("Valid Incoming Payment", "Received Tk 1,500.00 from 01612345678. Trx ID: UP98213A. Fee Tk 0.00"),
                Pair("Cash Out Alert", "Cash Out Tk 1,000.00 at Upay Agent. Trx ID: UP98213B")
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            shape = com.example.ui.theme.WisfyShapes.BottomSheetTop,
            color = com.example.ui.theme.LocalWisfyColors.current.canvasBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Provider Profiles (Section 18)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Configurable profiles for MFS providers with strict outgoing payment suppression and unverified status guarantees.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProviderProfiles.allProfiles.forEach { profile ->
                        val isSelected = selectedProfileId == profile.providerId
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ObsidianBlack else WarmSurfaceCard)
                                .border(1.dp, if (isSelected) ObsidianBlack else WarmBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedProfileId = profile.providerId }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = profile.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trusted Senders Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmSurfaceCard)
                        .border(1.dp, WarmBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TRUSTED SENDER IDENTIFIERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentProfile.trustedSenders.joinToString(", "),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("TEMPLATE & SAFETY PARSER PREVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                sampleTemplates.forEach { (label, text) ->
                    val senderForTest = if (label.contains("Spoofed")) "Unknown017" else currentProfile.trustedSenders.first()
                    val result = ParserRegistry.parseMessage(senderForTest, text)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmSurfaceCard)
                            .border(1.dp, WarmBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                StatusBadge(status = result.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = text,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                            if (result.isPaymentCandidate) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Candidate: ${result.currency} ${result.amount} | Trx: ${result.transactionId} (server_verified: false)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.EmeraldGreen,
                        contentColor = ObsidianBlack
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.Bold, color = ObsidianBlack)
                }
            }
        }
    }
}
