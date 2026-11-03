# Wisfy SMS Implementation Checklist

**Product:** Wisfy SMS  
**Developer Attribution:** Developed by Shahria Nafiz Sajjad  
**Architecture:** Open-source, backend-free Android SMS-to-Webhook application  

---

## Phase 1 — Foundation & Branding
- [x] Inspect updated PRD (including Section 18) and official logo `Wis-logo-light_33fcfcc7.png`
- [x] Configure Warm Obsidian Commerce design tokens, typography, and Material 3 theme
- [x] Integrate official Wisfy logo across adaptive launcher icon (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`, and mipmaps `mdpi` through `xxxhdpi`)
- [x] Update `metadata.json`, `strings.xml`, `settings.gradle.kts`, and `app/build.gradle.kts`
- [x] Display developer attribution **"Developed by Shahria Nafiz Sajjad"** across About screen, onboarding footer, and README

## Phase 2 — Local Storage & Persistent Two-Layer Deduplication (Section 18)
- [x] Define `WebhookEndpointEntity` Room entity with URL, auth token, headers, filters, retries, and test metrics
- [x] Define `SmsEventEntity` with unique `event_id`, sender, sanitized body, parsed provider/amount/status, and `idempotency_key`
- [x] Define `DeliveryLogEntity` with attempt counts, latency, HTTP codes, and responses
- [x] Define `ProcessedMessageFingerprint` entity & unique index for **Layer A incoming broadcast deduplication**
- [x] Define `ProcessedTransactionEntity` entity & unique index for **Layer B financial transaction deduplication**
- [x] Define `AppSettingsEntity` with privacy toggles (mask body in logs, forward raw body, OTP filter)
- [x] Build `WisfyDatabase` singleton with zero-cloud on-device persistence

## Phase 3 — SMS & Event Pipeline & Provider Safety Rules (Section 18.3 & 18.4)
- [x] Implement `WisfySmsReceiver` for `android.provider.Telephony.SMS_RECEIVED` with multipart assembly
- [x] Implement `WisfyForegroundService` with persistent low-priority notification
- [x] Implement `PrivacyFilter` for automated OTP and verification code detection and redaction
- [x] Implement strict provider profiles:
  - **bKash**: Trusted senders `bKash`, `BKASH`, `16247`. Outgoing cash-out/fee filtering.
  - **Nagad**: Trusted senders `NAGAD`, `Nagad`, `16167`. Outgoing send-money/debit filtering.
  - **Rocket**: Trusted senders `Rocket`, `ROCKET`, `16216`, `DBBL`. Outgoing/reversal filtering.
  - **Upay**: Trusted senders `UPAY`, `Upay`, `16268`. Outgoing filtering.
  - **Bank alerts**: Credit/deposit notifications.
- [x] Enforce safety rules:
  - Untrusted senders receive status `unknown_sender` and are not classified as payment candidates
  - Outgoing payments, cash-outs, reversals receive status `outgoing_filtered` and do not trigger financial webhooks
  - Candidates are strictly marked with `"server_verified": false`
- [x] Suppress duplicate financial webhooks on Layer A or Layer B matches

## Phase 4 — Webhook Delivery Engine (Schema v1.1)
- [x] Implement `WebhookDispatcher` with OkHttp, bounded timeout, and HTTPS enforcement
- [x] Format JSON payload conforming strictly to Schema v1.1 (`deduplication` object, `payment_candidate` object, `message` object)
- [x] Transmit required headers (`X-Wisfy-Event`, `X-Wisfy-Event-Id`, `X-Wisfy-Schema-Version`, `Authorization`)
- [x] Implement synthetic `wisfy.test` event ping for zero-risk webhook testing
- [x] Implement manual retry mechanism preserving stable `event_id` and idempotency key

## Phase 5 — UI Screens & Features
- [x] **Welcome / Get Started Screen:** Official glowing Wisfy logo card, feature pills, attribution, Continue CTA
- [x] **Permissions Setup Screen:** SMS explanation, battery optimization, local-first guarantee, agreement checkbox, Android permission request launcher
- [x] **Dashboard:** Service status badge, 4 metric cards (Delivered, Queued, Failed, Success Rate), Pipeline highlights card, Two-Layer Dedup indicator pill, MFS Profiles button, Live Dispatched Feed with filter tabs
- [x] **SMS Feed & Logs:** Search bar, filter chips, payload cards, "Inspect Payload" dialog, "Retry" button
- [x] **Endpoints Screen:** Endpoint cards with toggle switch, last ping metrics, "Test Webhook", "Edit", "Delete", Add/Edit dialog with Bearer token mask toggle
- [x] **Settings Screen:** Listener service toggle, battery optimization review, retry/timeout rules, privacy minimization toggles, local cache metrics, redacted diagnostic report export, clear data action
- [x] **Provider Profiles Dialog:** Interactive MFS template preview for bKash, Nagad, Rocket, Upay with trusted sender and safety parser feedback
- [x] **Simulate SMS Dialog:** Presets for bKash incoming, bKash cash-out, Nagad incoming, Rocket incoming, Duplicate transaction test, and OTP filter test
- [x] **Docs & Webhook API Dialog:** Schema v1.1 integration guide and runnable Node.js sample receiver

## Phase 6 — Verification & Delivery
- [x] Automated unit tests passing for parsers, outgoing suppression, duplicate checks, and Schema v1.1
- [x] Verified zero external calls to Wisfy servers (100% backend-free)
- [x] Build succeeded with `compile_applet`
