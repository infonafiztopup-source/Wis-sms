/**
 * Wisfy SMS Verified Sample Webhook Receiver (Schema v1.1)
 * Developed by Shahria Nafiz Sajjad
 * License: MIT
 */

import express from "express";
import crypto from "node:crypto";

const app = express();
app.use(express.json({ limit: "64kb" }));

const WEBHOOK_TOKEN = process.env.WISFY_WEBHOOK_TOKEN || "replace-with-your-token";

// In-memory sets for demonstration (in production, use database unique index on event_id and provider_transaction_key)
const seenEventIds = new Set();
const seenTransactionKeys = new Set();

app.post("/api/webhooks/wisfy-sms", (req, res) => {
  const authHeader = req.get("authorization") || "";
  const suppliedToken = authHeader.startsWith("Bearer ") ? authHeader.slice(7) : "";

  const expectedBuffer = Buffer.from(WEBHOOK_TOKEN);
  const suppliedBuffer = Buffer.from(suppliedToken);

  const isAuthValid =
    expectedBuffer.length === suppliedBuffer.length &&
    crypto.timingSafeEqual(expectedBuffer, suppliedBuffer);

  if (!isAuthValid) {
    return res.status(401).json({ success: false, error: "unauthorized" });
  }

  const payload = req.body;
  if (!payload || typeof payload.event_id !== "string" || typeof payload.event !== "string") {
    return res.status(400).json({ success: false, error: "invalid_payload" });
  }

  // 1. Event Idempotency Check
  if (seenEventIds.has(payload.event_id)) {
    console.log(`[Wisfy SMS] Duplicate event received: ${payload.event_id}. Acknowledging idempotently.`);
    return res.status(200).json({ success: true, duplicate: true, action: "acknowledged_no_op" });
  }
  seenEventIds.add(payload.event_id);

  // 2. Financial Transaction Deduplication
  const candidate = payload.payment_candidate;
  if (candidate && candidate.provider && candidate.transaction_id) {
    const txKey = `${candidate.provider}:${candidate.transaction_id}`;
    if (seenTransactionKeys.has(txKey)) {
      console.warn(`[Wisfy SMS] Transaction ID already processed: ${txKey}. Suppressing duplicate order fulfillment.`);
      return res.status(200).json({ success: true, duplicate_transaction: true, action: "suppressed" });
    }
    seenTransactionKeys.add(txKey);

    console.log(`[Payment Candidate] Provider: ${candidate.provider}, Amount: ${candidate.amount} ${candidate.currency}, TrxID: ${candidate.transaction_id}`);
    console.log(`[Safety Notice] Status: ${candidate.status}, Server Verified: ${candidate.server_verified}. (Authoritative verification must be completed server-side).`);
  }

  return res.status(200).json({ success: true, received: true });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Wisfy SMS Schema v1.1 receiver listening on port ${PORT}`);
});
