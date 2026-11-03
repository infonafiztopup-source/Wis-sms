# Wisfy SMS Webhook API Specification

**Developed by Shahria Nafiz Sajjad**  
Schema Version: `1.1`

---

## 1. Request Contract

All webhooks dispatched from Wisfy SMS use HTTPS POST requests with a UTF-8 JSON payload.

### Standard Headers

| Header | Example | Description |
|---|---|---|
| `Content-Type` | `application/json` | Standard media type |
| `X-Wisfy-Event` | `sms.received` / `wisfy.test` | Event descriptor |
| `X-Wisfy-Event-Id` | `evt_l9a4k1_8f93` | Stable idempotency key |
| `X-Wisfy-Schema-Version` | `1.1` | Payload version |
| `Authorization` | `Bearer <token>` | Sent only if configured |

---

## 2. JSON Event Schema (v1.1)

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
    "body": "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. TrxID 9K8A7L6M2P",
    "sim_slot": 1,
    "received_at": "2026-10-31T18:30:00+06:00"
  }
}
```

> **Instant Website Integration:** Looking to build payment verification on your website? See [`docs/ai-integration-prompt.md`](./ai-integration-prompt.md) to copy a prompt directly into Claude or ChatGPT to generate your backend receiver in seconds!

---

## 3. Persistent Two-Layer Deduplication (PRD Section 18)

1. **Layer A — Incoming Broadcast Deduplication**:
   - SHA-256 fingerprint generated from sender address, normalized body, and received timestamp bucket.
   - Duplicate message broadcasts are suppressed so they cannot trigger repeated webhook requests.
2. **Layer B — Payment Transaction Deduplication**:
   - Unique key generated from `provider:transaction_id` (e.g. `bkash:9K8A7L6M2P`).
   - A transaction reference already processed for that provider will never trigger a second payment webhook.
   - Retries preserve the exact same `event_id` and `idempotency_key`.

---

## 4. Payment Safety Warning

An SMS is not authoritative proof of payment. Sender labels can be spoofed in cellular networks. Extracted fields always have `"server_verified": false`. Receiving servers must verify payments via authoritative merchant APIs or reconciliation before fulfilling orders.
