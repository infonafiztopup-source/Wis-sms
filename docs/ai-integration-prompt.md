# Claude & Codex Integration Prompt for Wisfy SMS Payment Gateway

> **Copy and paste the prompt below directly into Claude (3.7 / 3.5 Sonnet), OpenAI ChatGPT, or Codex.**  
> It instructs the AI to immediately generate a complete, production-ready payment receiver and order reconciliation backend for your website or e-commerce store.

---

```markdown
You are a senior backend and payment gateway engineer. I am using "Wisfy SMS" (an open-source Android SMS-to-Webhook bridge developed by Shahria Nafiz Sajjad) to automate merchant payment verification on my e-commerce website. 

When my merchant phone receives an MFS SMS from bKash, Nagad, Rocket, or Upay, Wisfy SMS intercepts the broadcast, performs two-layer deduplication, parses the payment amount and transaction ID (TrxID), detects the receiving SIM card (SIM 1 or SIM 2), and dispatches an HTTPS POST webhook to my server.

Please write the complete payment webhook receiver and order fulfillment logic for my website.

### My Tech Stack
(Select or specify your stack: e.g. Next.js 14 App Router with TypeScript & Prisma, Node.js Express with MongoDB, PHP / Laravel with MySQL, or Python FastAPI with PostgreSQL).

---

### 1. Wisfy SMS Webhook Request Contract

- **HTTP Method:** `POST`
- **Path:** `/api/webhooks/wisfy-sms`
- **Content-Type:** `application/json; charset=utf-8`
- **Authentication:** `Authorization: Bearer <WISFY_SECRET_TOKEN>`
- **Custom Headers:**
  - `X-Wisfy-Event`: `sms.received` or `wisfy.test`
  - `X-Wisfy-Event-Id`: Stable UUID idempotency identifier (e.g. `evt_l9a4k1_8f93`)
  - `X-Wisfy-Schema-Version`: `1.1`

---

### 2. JSON Event Payload Schema (Version 1.1)

```json
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
    "body": "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30",
    "sim_slot": 1,
    "received_at": "2026-10-31T18:30:00+06:00"
  }
}
```

---

### 3. Business & Verification Rules You Must Implement

1. **Security & Authentication:**
   - Verify `Authorization: Bearer <WISFY_SECRET_TOKEN>` using constant-time string comparison. Return `401 Unauthorized` if invalid.
2. **Handling Synthetic Diagnostic Tests:**
   - If `event_type == "wisfy.test"` or `parsed.provider == "system_test"`, log the test ping and return `200 OK` with `{"success": true, "test": true}` immediately without modifying database records.
3. **Two-Layer Deduplication & Idempotency:**
   - Record `event_id` and `deduplication.idempotency_key` (e.g. `bkash:9K8A7L6M2P`) in an `IdempotencyLog` or `Payments` database table with a unique constraint.
   - If already recorded, return `200 OK` with `{"success": true, "duplicate": true}` immediately to acknowledge delivery and prevent double credit.
4. **Validating Payment Candidates:**
   - Ignore outgoing transactions: only proceed if `event_type == "sms.payment_candidate"` and `payment_candidate.status == "parsed_candidate"`.
   - Never trust SMS alone blindly: Note that `server_verified: false` is expected because cellular SMS sender IDs can theoretically be spoofed.
5. **Matching Customer Order:**
   - Find a pending customer order where `status == "PENDING_PAYMENT"` and `order.total_amount == payment_candidate.amount`.
   - (Optional: check if customer input matching TrxID in their checkout page, or if customer phone number matches).
   - If matched:
     - Mark order `status = "PAID"`
     - Store `payment_method = payment_candidate.provider` (bkash/nagad/rocket/upay)
     - Store `transaction_id = payment_candidate.transaction_id`
     - Record `sim_slot = payload.sim_slot`
     - Trigger order fulfillment or send confirmation email to customer.
   - If no pending order matches:
     - Save to an `UnmatchedPayments` table for manual admin reconciliation.
6. **Execution Speed:**
   - Return HTTP `200 OK` within 5 seconds so the Android phone doesn't trigger network retries.

Please generate:
1. The database schema / migration for payments and idempotency keys.
2. The complete webhook route handler code.
3. Order matching and status transition logic inside a database transaction.
4. Clean instructions for setting up the environment variable `WISFY_SECRET_TOKEN`.
```
