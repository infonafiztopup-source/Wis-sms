package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.WisfyBrandShape
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder

private const val CLAUDE_CODEX_PROMPT = """
You are a senior backend and payment gateway engineer. I am using "Wisfy SMS" (an Android SMS-to-Webhook bridge developed by Shahria Nafiz Sajjad) to automate merchant payment verification on my e-commerce website.

When my merchant phone receives an MFS SMS from bKash, Nagad, Rocket, or Upay, Wisfy SMS intercepts the broadcast, performs two-layer deduplication, parses the payment amount and transaction ID (TrxID), detects the receiving SIM card (SIM 1 or SIM 2), and dispatches an HTTPS POST webhook to my server.

Please write the complete payment webhook receiver and order fulfillment logic for my website. Support whichever stack I specify (PHP / Laravel / WordPress, Node.js / Express / Next.js, or HTML / Vanilla JS).

### 1. Wisfy SMS Webhook Request Contract
- HTTP Method: POST /api/webhooks/wisfy-sms
- Headers:
  - Content-Type: application/json; charset=utf-8
  - Accept: application/json, text/plain, */*
  - User-Agent: Wisfy-Gateway/1.2 (Android; PHP-Node-HTML-Compatible)
  - Authorization: Bearer <WISFY_SECRET_TOKEN>
  - X-Wisfy-Event: sms.received / wisfy.test
  - X-Wisfy-Event-Id: <idempotency_key>
  - X-Wisfy-Schema-Version: 1.1
  - X-Wisfy-Sim-Slot: 1 or 2

### 2. JSON Event Payload Schema (v1.1)
{
  "event": "sms.received",
  "event_type": "sms.payment_candidate",
  "event_id": "evt_l9a4k1_8f93",
  "schema_version": "1.1",
  "device_id": "device_9b2e04f",
  "received_at": "2026-10-31T18:30:00+06:00",
  "sim_slot": 1,
  "deduplication": {
    "message_duplicate": false,
    "transaction_duplicate": false,
    "idempotency_key": "bkash:9K8A7L6M2P"
  },
  "payment_candidate": {
    "provider": "bkash",
    "status": "parsed_candidate",
    "amount": "2500.00",
    "currency": "BDT",
    "transaction_id": "9K8A7L6M2P",
    "sender_match": true,
    "template_match": true,
    "server_verified": false
  },
  "message": {
    "sender": "bKash",
    "body": "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. TrxID 9K8A7L6M2P",
    "sim_slot": 1,
    "received_at": "2026-10-31T18:30:00+06:00"
  }
}

### 3. Business & Verification Rules To Implement
1. Verify Bearer token authorization header. Return 401 Unauthorized if invalid.
2. If event_type == "wisfy.test", return 200 OK {"success": true, "test": true} without touching database.
3. Check deduplication.idempotency_key in database. If already processed, return 200 OK {"success": true, "duplicate": true} immediately to avoid double credit.
4. If payment_candidate.status == "parsed_candidate", match pending order where order.total_amount == payment_candidate.amount.
5. In a database transaction:
   - Mark order status = "PAID"
   - Record transaction_id, provider, sim_slot
   - Record idempotency_key to prevent replaying
6. Return HTTP 200 within 5 seconds so mobile client does not trigger network retries.
"""

private val SAMPLE_PHP_RECEIVER = """<?php
// ========================================================
// Wisfy SMS Verified Webhook Receiver (PHP / Laravel / WordPress)
// Developed by Shahria Nafiz Sajjad
// ========================================================

header('Content-Type: application/json; charset=utf-8');

___D___expectedToken = getenv('WISFY_WEBHOOK_TOKEN') ?: 'your_secret_token';

// 1. Verify Authorization Header (Bearer Token)
___D___headers = getallheaders();
___D___authHeader = ___D___headers['Authorization'] ?? ___D___headers['authorization'] ?? '';
___D___token = trim(str_ireplace('Bearer ', '', ___D___authHeader));

if (empty(___D___token) || ___D___token !== ___D___expectedToken) {
    http_response_code(401);
    echo json_encode(['success' => false, 'error' => 'Unauthorized']);
    exit;
}

// 2. Read Raw JSON Payload from php://input
___D___rawInput = file_get_contents('php://input');
___D___data = json_decode(___D___rawInput, true);

if (!___D___data) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid JSON']);
    exit;
}

// 3. Handle Synthetic Diagnostic Ping from Wisfy Gateway
if ((___D___data['event_type'] ?? '') === 'wisfy.test') {
    http_response_code(200);
    echo json_encode(['success' => true, 'test' => true, 'runtime' => 'PHP ' . PHP_VERSION]);
    exit;
}

// 4. Extract Event Data
___D___eventId = ___D___data['event_id'] ?? '';
___D___simSlot = ___D___data['sim_slot'] ?? 1;
___D___dedup = ___D___data['deduplication'] ?? [];
___D___idempotencyKey = ___D___dedup['idempotency_key'] ?? ___D___eventId;
___D___payment = ___D___data['payment_candidate'] ?? null;

// 5. Database Idempotency Check (Prevent duplicate orders)
// Example: SELECT id FROM payment_logs WHERE idempotency_key = ?
___D___isDuplicate = false; // Query your database here
if (___D___isDuplicate) {
    http_response_code(200);
    echo json_encode(['success' => true, 'duplicate' => true]);
    exit;
}

// 6. Match Order & Fulfill Payment
if (!empty(___D___payment['amount']) && !empty(___D___payment['transaction_id'])) {
    ___D___amount = (float) ___D___payment['amount'];
    ___D___trxId = ___D___payment['transaction_id'];
    ___D___provider = ___D___payment['provider'] ?? 'unknown';

    // Example MySQL PDO update:
    // ___D___stmt = ___D___pdo->prepare("UPDATE orders SET status = 'PAID', trx_id = ?, provider = ?, sim = ? WHERE total = ? AND status = 'PENDING' LIMIT 1");
    // ___D___stmt->execute([___D___trxId, ___D___provider, ___D___simSlot, ___D___amount]);
}

http_response_code(200);
echo json_encode([
    'success' => true,
    'received' => true,
    'sim_slot' => ___D___simSlot,
    'trx_id' => ___D___payment['transaction_id'] ?? null
]);
""".trimIndent().replace("___D___", "$")

private val SAMPLE_NODE_RECEIVER = """// ========================================================
// Wisfy SMS Verified Webhook Receiver (Node.js / Express)
// Developed by Shahria Nafiz Sajjad
// ========================================================

import express from "express";

const app = express();
// Enable JSON body parser with UTF-8 support
app.use(express.json({ limit: "64kb" }));

const WEBHOOK_TOKEN = process.env.WISFY_WEBHOOK_TOKEN || "your_secret_token";
const seenKeys = new Set(); // Use database UNIQUE constraint in production

app.post("/api/webhooks/wisfy-sms", (req, res) => {
  const auth = req.get("authorization") || "";
  const token = auth.replace(/^Bearer\s+/i, "").trim();
  
  if (token !== WEBHOOK_TOKEN) {
    return res.status(401).json({ success: false, error: "Unauthorized" });
  }

  const { event_type, event_id, deduplication, payment_candidate, sim_slot } = req.body;
  
  // Synthetic diagnostic ping from Wisfy app
  if (event_type === "wisfy.test") {
    return res.status(200).json({ success: true, test: true, runtime: "Node.js" });
  }

  const idempotencyKey = deduplication?.idempotency_key || event_id;
  if (seenKeys.has(idempotencyKey)) {
    return res.status(200).json({ success: true, duplicate: true });
  }
  seenKeys.add(idempotencyKey);

  console.log(`[SIM ___D___{sim_slot || 1}] Event: ___D___{event_id}`);
  if (payment_candidate?.amount) {
    console.log(`Payment: ___D___{payment_candidate.amount} ___D___{payment_candidate.currency} TrxID: ___D___{payment_candidate.transaction_id}`);
    // TODO: Fulfill matching pending order in database
  }

  return res.status(200).json({ success: true, received: true });
});

app.listen(3000, () => console.log("Wisfy Receiver listening on port 3000"));
""".trimIndent().replace("___D___", "$")

private val SAMPLE_HTML_CURL_RECEIVER = """# ========================================================
# 1. Test your webhook endpoint with cURL from Terminal:
# ========================================================

curl -X POST "https://yourdomain.com/api/webhooks/wisfy-sms" \
  -H "Content-Type: application/json; charset=utf-8" \
  -H "Authorization: Bearer your_secret_token" \
  -H "X-Wisfy-Event: wisfy.test" \
  -d '{
    "event": "wisfy.test",
    "event_type": "wisfy.test",
    "schema_version": "1.1",
    "sim_slot": 1
  }'

# ========================================================
# 2. Browser JavaScript Fetch Test Snippet:
# ========================================================

async function testWisfyWebhook(url, secretToken) {
  const res = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      "Authorization": `Bearer ___D___{secretToken}`,
      "X-Wisfy-Event": "wisfy.test"
    },
    body: JSON.stringify({
      event: "wisfy.test",
      event_type: "wisfy.test",
      schema_version: "1.1",
      sim_slot: 1
    })
  });
  return await res.json();
}
""".trimIndent().replace("___D___", "$")


@Composable
fun DocsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("ai_prompt") } // "ai_prompt", "php", "node", "html"
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        WisfyBrandShape(modifier = Modifier.size(28.dp), contentDescription = "Wisfy Logo")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Webhook API & Integration", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-Stack Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabPill(
                        label = "AI Prompt",
                        isSelected = selectedTab == "ai_prompt",
                        onClick = { selectedTab = "ai_prompt" },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        label = "PHP",
                        isSelected = selectedTab == "php",
                        onClick = { selectedTab = "php" },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        label = "Node.js",
                        isSelected = selectedTab == "node",
                        onClick = { selectedTab = "node" },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        label = "HTML / cURL",
                        isSelected = selectedTab == "html",
                        onClick = { selectedTab = "html" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    "ai_prompt" -> {
                        // AI Prompt Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldLight)
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("One-Click AI Integration Prompt", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldDark)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Paste this comprehensive prompt into Claude (3.7 Sonnet) or ChatGPT/Codex. It instructs the AI to generate the exact webhook receiver and database order fulfillment logic for your PHP, Node.js, or HTML web project!",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Copy Button
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Wisfy AI Prompt", CLAUDE_CODEX_PROMPT.trim()))
                                Toast.makeText(context, "Prompt copied! Paste into Claude or ChatGPT.", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("copy_ai_prompt_button"),
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ObsidianBlack,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Prompt for Claude / Codex", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CodeBox(code = CLAUDE_CODEX_PROMPT.trim())
                    }
                    "php" -> {
                        CodeHeaderWithCopy(
                            title = "PHP / LARAVEL / WORDPRESS WEBHOOK (webhook.php)",
                            code = SAMPLE_PHP_RECEIVER,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CodeBox(code = SAMPLE_PHP_RECEIVER.trim())
                    }
                    "node" -> {
                        CodeHeaderWithCopy(
                            title = "NODE.JS / EXPRESS WEBHOOK (server.js)",
                            code = SAMPLE_NODE_RECEIVER,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CodeBox(code = SAMPLE_NODE_RECEIVER.trim())
                    }
                    "html" -> {
                        CodeHeaderWithCopy(
                            title = "HTML / VANILLA JS & TERMINAL CURL",
                            code = SAMPLE_HTML_CURL_RECEIVER,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CodeBox(code = SAMPLE_HTML_CURL_RECEIVER.trim())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBlack,
                        contentColor = Color.White
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ObsidianBlack else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun CodeHeaderWithCopy(
    title: String,
    code: String,
    context: Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Wisfy Sample Code", code.trim()))
                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy code", modifier = Modifier.size(16.dp), tint = ObsidianBlack)
        }
    }
}

@Composable
private fun CodeBox(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Text(
            text = code,
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}
