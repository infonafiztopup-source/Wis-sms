# Wisfy SMS

**Developed by Shahria Nafiz Sajjad**  
License: MIT  

Wisfy SMS is an open-source, backend-free Android SMS-to-Webhook application that forwards user-authorized incoming SMS events directly from an Android device to one or more user-configured HTTP(S) webhook endpoints.

### Direct APK Download
The pre-built APK is available directly in the root of the repository:
- **[Download Wis-sms.apk](./Wis-sms.apk)**

---

## Key Features

- **Backend-Free & Local-First:** No central Wisfy accounts, cloud servers, Firebase, Supabase, or telemetry. Configuration, queue, and logs stay on your device.
- **Direct HTTPS POST:** Transmits versioned JSON events directly to any destination API, merchant gateway, or automation server.
- **Provider Normalization:** Auto-detects and extracts payment information from supported providers (bKash, Nagad, Rocket, Upay, Bank alerts) while marking payment status as `unverified` (actual fulfillment verification belongs to the receiving system).
- **Privacy & Minimization:** 
  - Automated OTP and 2FA passcode detection and filtering.
  - Optional body masking in logs.
  - Selective raw message forwarding toggle.
- **Deduplication & Idempotency:** Assigns unique `event_id` tokens used as idempotency keys.
- **Delivery Inspection & Retries:** Inspect raw and parsed payloads in real-time, view HTTP latency and response codes, and manually retry transient failures.
- **Warm Obsidian Design:** Designed according to the Warm Obsidian Commerce specification with light tactile surfaces, deep obsidian button accents, and emerald pipeline status indicators.

---

## Webhook Architecture

```text
Android Phone (Wisfy SMS)
        │
        │ HTTPS POST (JSON payload)
        ▼
Your Website / Webhook Endpoint
        │
        ▼
Validate Token -> Deduplicate event_id -> Trigger business action
```

### Request Headers

```http
Content-Type: application/json
X-Wisfy-Event: sms.received
X-Wisfy-Event-Id: evt_example_001
X-Wisfy-Schema-Version: 1.0
Authorization: Bearer <user-configured-token>
```

### JSON Event Schema

```json
{
  "event": "sms.received",
  "event_id": "evt_example_001",
  "schema_version": "1.0",
  "device_id": "device_local_random_id",
  "received_at": "2026-10-31T18:30:00+06:00",
  "message": {
    "sender": "bKash",
    "body": "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. TrxID 9K8A7L6M2P",
    "received_at": "2026-10-31T18:30:00+06:00"
  },
  "parsed": {
    "provider": "bkash",
    "transaction_id": "9K8A7L6M2P",
    "amount": 2500.00,
    "currency": "BDT",
    "status": "unverified"
  }
}
```

---

## Documentation

- [Webhook API Specification](docs/webhook-api.md)
- [Website Integration Guide](docs/website-integration.md)
- [Runnable Node.js Example](examples/webhook-receiver-node/server.js)

---

## Attribution & License

- **Developer:** Shahria Nafiz Sajjad
- **License:** MIT License
