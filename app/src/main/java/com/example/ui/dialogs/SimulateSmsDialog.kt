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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.WarmSurfaceCard

@Composable
fun SimulateSmsDialog(
    onDismiss: () -> Unit,
    onSimulate: (sender: String, body: String, simSlot: Int) -> Unit
) {
    var sender by remember { mutableStateOf("bKash") }
    var body by remember { mutableStateOf("You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30") }
    var simSlot by remember { mutableStateOf(1) } // 1 or 2

    val scrollState = rememberScrollState()

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
                        Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simulate SMS Event", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Test two-layer deduplication, candidate parsing, and privacy filters without physical SMS broadcasts.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "TEST PRESETS (SECTION 18)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Presets Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip("bKash In: Tk 2.5K", Modifier.weight(1f)) {
                        sender = "bKash"
                        body = "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30"
                    }
                    PresetChip("bKash Out: Cash-out", Modifier.weight(1f)) {
                        sender = "bKash"
                        body = "Cash Out Tk 500.00 to 01712345678. Fee Tk 7.50. Balance Tk 14,733.00. TrxID 9K8A7L6M2Q at 31/10/2026 18:32"
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip("Nagad In: Tk 1.2K", Modifier.weight(1f)) {
                        sender = "NAGAD"
                        body = "Money received. Amount: Tk 1,200.00 from 01987654321. TxnID: 7X89QPL2. Balance: Tk 6,400.00 at 31/10/2026"
                    }
                    PresetChip("Rocket In: Tk 3.0K", Modifier.weight(1f)) {
                        sender = "Rocket"
                        body = "Tk 3,000.00 received from 01812345678. TxnId: 8A99LKJ1. Balance Tk 8,500.00"
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetChip("Duplicate Tx Test", Modifier.weight(1f)) {
                        // Same TrxID 9K8A7L6M2P to demonstrate Layer B deduplication suppression!
                        sender = "bKash"
                        body = "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30"
                    }
                    PresetChip("OTP Filter Test", Modifier.weight(1f)) {
                        sender = "GoogleAuth"
                        body = "Your verification code is 849201. Do not share this code with anyone."
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sender input
                Text("Sender Identifier / Address", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = sender,
                    onValueChange = { sender = it },
                    placeholder = { Text("e.g. bKash, NAGAD, 16216") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_sender_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WarmSurfaceCard,
                        unfocusedContainerColor = WarmSurfaceCard,
                        focusedBorderColor = ObsidianBlack,
                        unfocusedBorderColor = WarmBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Body input
                Text("Message Body", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    placeholder = { Text("Enter message text...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("simulate_body_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WarmSurfaceCard,
                        unfocusedContainerColor = WarmSurfaceCard,
                        focusedBorderColor = ObsidianBlack,
                        unfocusedBorderColor = WarmBorder
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated SIM Slot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SIM Card Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (simSlot == 1) ObsidianBlack else WarmSurfaceCard)
                                .border(1.dp, if (simSlot == 1) ObsidianBlack else WarmBorder, RoundedCornerShape(8.dp))
                                .clickable { simSlot = 1 }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("SIM 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (simSlot == 1) Color.White else TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (simSlot == 2) ObsidianBlack else WarmSurfaceCard)
                                .border(1.dp, if (simSlot == 2) ObsidianBlack else WarmBorder, RoundedCornerShape(8.dp))
                                .clickable { simSlot = 2 }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("SIM 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (simSlot == 2) Color.White else TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit button
                Button(
                    onClick = {
                        if (sender.isNotBlank() && body.isNotBlank()) {
                            onSimulate(sender.trim(), body.trim(), simSlot)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("simulate_submit_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.EmeraldGreen,
                        contentColor = ObsidianBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger & Forward SMS Event", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WarmSurfaceCard)
            .border(1.dp, WarmBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
