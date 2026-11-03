# Wisfy SMS

## Product Requirements Document (PRD)

**Product:** Wisfy SMS\
**Product type:** Open-source, backend-free Android SMS-to-Webhook
application\
**Developer attribution:** Developed by Shahria Nafiz Sajjad\
**Repository name:** `wisfy-sms`\
**License proposal:** MIT\
**Document status:** Implementation-ready specification\
**Primary platform:** Android\
**UI reference:** User-provided Wisfy SMS Forwarder reference image

------------------------------------------------------------------------

## 1. Project Overview

Wisfy SMS is an open-source Android application that forwards
user-authorized incoming SMS events directly from an Android device to
one or more user-configured HTTP(S) webhook endpoints.

The application is intended to work with many independent systems,
including payment gateways, merchant applications, e-commerce platforms,
SaaS products, ERP systems, custom APIs, and automation services. It
must not be hard-coded for Mahiru Pay, AeroHealth, PipraPay, or any
single provider.

**Core principle:** the app is a generic SMS-to-webhook bridge. It
collects and forwards SMS events; the receiving application remains
responsible for validating, interpreting, and acting on those events.

### 1.1 Non-negotiable requirements

-   No Wisfy-hosted backend.
-   No required Supabase, Firebase, cloud database, central account
    system, or remote Wisfy service.
-   No mandatory analytics, telemetry, tracking, or crash-reporting
    service.
-   Configuration, queue, and delivery logs are stored locally on the
    device.
-   Users configure their own webhook endpoints.
-   Support generic HTTPS POST webhooks with JSON payloads.
-   Support multiple independent endpoints and routing rules.
-   Use the reference image as the visual direction, not as a
    requirement to copy every pixel.
-   Show **"Developed by Shahria Nafiz Sajjad"** in the About screen and
    README.
-   Document all webhook behavior and provide a sample receiver for
    testing.
-   Do not claim that an SMS proves a payment has succeeded.

The app may optionally support user-operated infrastructure in the
future, but the initial release must not depend on any backend.

------------------------------------------------------------------------

## 2. Goals and Non-goals

### 2.1 Goals

1.  Receive eligible incoming SMS messages on an Android device with
    user consent.
2.  Let users configure one or more webhook endpoints.
3.  Forward SMS events directly from the phone to configured endpoints.
4.  Provide optional parsing for supported SMS formats while preserving
    the original event where configured.
5.  Provide filtering by sender, keyword, provider, and event type.
6.  Persist pending events locally and retry transient delivery
    failures.
7.  Provide transparent delivery history and endpoint testing.
8.  Support different backend stacks through a documented, versioned
    JSON schema.
9.  Keep the app usable without registration or a Wisfy cloud account.
10. Publish source code and contribution/build documentation.

### 2.2 Non-goals

-   Hosting a webhook relay or cloud dashboard.
-   Providing a central API key service.
-   Verifying payments by itself.
-   Guaranteeing that an SMS is genuine or that a payment has settled.
-   Bypassing Android permissions, battery controls, or operating-system
    restrictions.
-   Reading OTPs, passwords, or unrelated private messages by default.
-   Supporting every MFS SMS format without testing and a parser.
-   Claiming compatibility with a provider unless its SMS format has
    been tested.
-   Guaranteeing instant background delivery on every Android device.

------------------------------------------------------------------------

## 3. Target Users and Use Cases

### 3.1 Target users

-   Developers who need SMS events sent to their own APIs.
-   Merchants who receive payment notifications on an Android device.
-   Operators of self-hosted applications.
-   Developers building custom automation workflows.
-   Open-source contributors who want to add tested SMS parsers or
    delivery features.

### 3.2 Example use cases

-   Forward a payment notification SMS to a merchant-owned API.
-   Send selected SMS messages to a self-hosted webhook.
-   Route messages from different sender IDs to different endpoints.
-   Test a webhook and inspect its HTTP response.
-   Review pending, successful, and failed deliveries while offline.
-   Export a redacted diagnostic report without exporting secrets.

------------------------------------------------------------------------

## 4. Platform and Technology

### 4.1 Proposed stack

-   **UI:** Flutter.
-   **Android-specific SMS and background behavior:** Kotlin native code
    where required.
-   **Local persistence:** SQLite through a maintained Flutter database
    package.
-   **HTTP:** a maintained HTTP client such as Dio, or an equivalent
    client.
-   **Secrets:** Android Keystore-backed secure storage through a
    maintained library.
-   **Background retry:** Android WorkManager for deferrable work,
    combined with an appropriate native implementation for SMS reception
    and prompt processing.
-   **Tests:** Flutter unit/widget tests and Android-specific tests
    where practical.
-   **Build:** Gradle and Flutter toolchain.

Select package versions that are compatible at implementation time. Do
not assume a particular library API without checking its current
documentation.

### 4.2 Platform scope

The initial release targets Android. iOS support is not part of the
initial scope because iOS does not offer equivalent general-purpose
incoming SMS access for this use case.

The application must handle Android version differences and
device-manufacturer background restrictions gracefully.

------------------------------------------------------------------------

## 5. UX and Visual Design

Follow the user-provided reference image: clean, premium, mobile-first
UI with white/light surfaces, black primary buttons, rounded cards,
subtle shadows, small green health indicators, and clear typography. A
dark dotted background may be used in promotional mockups, but the
actual app screens should remain readable and accessible.

### 5.1 Required screens

#### A. Welcome / Get Started

-   Wisfy SMS brand mark and app name.
-   Short explanation: "Forward selected SMS events to your own
    webhook."
-   Supported use-case labels, without implying untested compatibility.
-   Privacy summary explaining that messages go to endpoints the user
    configures.
-   Continue button.

#### B. Permissions Setup

-   Explain each requested permission before prompting.
-   Request only permissions needed for the selected functionality.
-   Show granted, denied, or restricted state.
-   Provide instructions for changing permissions in Android Settings.
-   Do not repeatedly prompt after a user declines.
-   Never disguise permission requests or claim that access is optional
    when a feature requires it.

#### C. Dashboard

-   SMS listener/service status.
-   Current endpoint status, if configured.
-   Counts for delivered, queued, and failed events.
-   Latest delivery time and latest HTTP status.
-   Recent activity list.
-   Quick actions: Test Webhook, Add Endpoint, View Logs.
-   Clear distinction between "SMS received" and "webhook delivered."

#### D. Connect Webhook / Add Endpoint

Fields: - Endpoint name. - HTTPS webhook URL. - HTTP method (POST as
default; other methods only if implemented safely). - Authentication
type: none, Bearer token, or configurable headers. - Event types to
send. - Sender/provider filters. - Optional custom headers. - Request
timeout. - Retry policy. - Enabled/disabled switch. - Test Webhook
button. - Save button.

Credentials must be masked in the UI and excluded from logs and exported
diagnostics.

#### E. Endpoint Details

-   Endpoint URL with sensitive query parameters hidden.
-   Last test result.
-   Last delivery time.
-   Recent success/failure counts.
-   Edit, disable, test, and delete actions.
-   Confirmation before deleting endpoint configuration.

#### F. Routing Rules

-   Create, edit, enable, disable, and delete rules.
-   Match by sender ID/number, provider parser, keyword, or event type.
-   Select one or more destinations.
-   Configure whether a matching event can go to multiple endpoints.
-   Provide rule priority and a clear "no matching rule" behavior.
-   Show a preview of which endpoints would match a sample event without
    sending it.

#### G. Delivery Logs

-   Filter by status, endpoint, date, and event type.
-   Show event ID, received time, attempt count, response status, and a
    redacted error.
-   Show payload preview with sensitive fields masked by default.
-   Manual retry for eligible failed events.
-   Delete local event data.
-   Do not show authorization tokens or unredacted secrets.

#### H. Settings

-   Listener/service controls and status.
-   Default retry configuration.
-   Data retention controls.
-   Export/import configuration with secrets excluded by default.
-   Local data deletion.
-   Privacy and permissions explanation.
-   App version and open-source license notices.

#### I. About

-   Name: Wisfy SMS.
-   Description: Open-source SMS-to-webhook Android app.
-   Developer: **Shahria Nafiz Sajjad**.
-   Source repository link configured at build time or in project
    documentation.
-   License information.
-   Third-party open-source notices.

------------------------------------------------------------------------

## 6. Functional Requirements

### FR-01: SMS Reception

-   Receive only messages the app is authorized to access under Android
    policy and OS permissions.
-   Record the SMS reception timestamp when available.
-   Preserve sender information and message content only to the extent
    required by configured rules.
-   Handle multipart SMS correctly where feasible.
-   Avoid processing the same incoming message repeatedly when the OS
    delivers duplicate callbacks.
-   Clearly indicate when the listener cannot operate due to missing
    permissions or OS restrictions.

### FR-02: Privacy-First Filtering

-   Do not forward all messages automatically on first launch.
-   Require the user to configure an endpoint and explicitly enable
    forwarding.
-   Support sender/provider allowlists and optional keyword rules.
-   Provide a denylist for senders or patterns the user does not want
    forwarded.
-   Do not forward OTPs or authentication codes by default where they
    can be identified reliably.
-   Do not assume every message from a financial brand is a payment
    notification.
-   Allow users to disable raw message body forwarding and send only
    selected parsed fields.
-   Explain that filtering is best-effort and cannot reliably identify
    every sensitive message format.

### FR-03: Local Event Normalization

Normalize an eligible SMS into a versioned internal event.

Recommended event fields: - `event` - `event_id` - `schema_version` -
`device_id` (random local identifier; no hardware identifier) -
`received_at` - `message.sender` - `message.body` (only when enabled by
configuration) - `message.received_at` - `parsed.provider` -
`parsed.transaction_id` - `parsed.amount` - `parsed.currency` -
`parsed.status`

Parser behavior: - Parser support must be explicit and testable. -
Unknown fields must not be guessed. - Missing values should be `null` or
omitted according to the schema. - Store parser confidence or parsing
warnings internally if useful. - Treat extracted payment details as
unverified data.

### FR-04: Multiple Endpoints

-   Users can configure multiple endpoints.
-   Each endpoint has its own URL, authentication, event filters, sender
    filters, and retry policy.
-   Disabling one endpoint must not disable other endpoints.
-   A single event may be sent to multiple endpoints only when the user
    explicitly configures that behavior.
-   Each endpoint delivery has an independent status and retry count.
-   Provide a safe Test Webhook action with a synthetic test payload; it
    must not transmit a real SMS body.

### FR-05: Webhook Request

Default: - HTTPS. - HTTP POST. - `Content-Type: application/json`. -
JSON schema version included in the body. - Unique event ID in both body
and `X-Wisfy-Event-Id` header. - Event name in `X-Wisfy-Event` header. -
Optional user-configured authentication. - Configurable timeout within a
safe bounded range.

Suggested headers:

``` http
Content-Type: application/json
X-Wisfy-Event: sms.received
X-Wisfy-Event-Id: <unique-event-id>
X-Wisfy-Schema-Version: 1.0
Authorization: Bearer <user-configured-token>
```

The Authorization header is included only when the user configures it.
Never include the literal placeholder in a real request.

### FR-06: Example Payload

``` json
{
  "event": "sms.received",
  "event_id": "evt_example_001",
  "schema_version": "1.0",
  "device_id": "device_local_random_id",
  "received_at": "2026-10-29T09:30:00+06:00",
  "message": {
    "sender": "ExampleSender",
    "body": "Example notification content",
    "received_at": "2026-10-29T09:29:58+06:00"
  },
  "parsed": {
    "provider": "unknown",
    "transaction_id": null,
    "amount": null,
    "currency": null,
    "status": "unverified"
  }
}
```

This is an illustrative payload, not a real payment record. Actual
timestamps must come from the device/event. Do not hardcode the example
values. The app should support a privacy mode that omits or redacts
`message.body`.

### FR-07: Webhook Delivery and HTTP Results

-   Treat 2xx responses as successful HTTP delivery.
-   Do not label a delivery "payment verified" based on a 2xx response.
-   Treat network errors, timeouts, HTTP 408, HTTP 429, and appropriate
    5xx responses as retryable.
-   Treat most other 4xx responses as non-retryable by default, with a
    visible failure reason; allow safe configuration only where
    appropriate.
-   Respect `Retry-After` when practical and within configured limits.
-   Store response status, attempt count, and a redacted response
    excerpt.
-   Limit response body size before storing it.
-   Do not follow redirects to unvalidated destinations automatically.
-   Avoid retry storms using exponential backoff with jitter.
-   Make retry policy visible and configurable.

### FR-08: Local Queue and Delivery State

Suggested states: - `received` - `filtered` - `queued` - `sending` -
`delivered` - `retry_scheduled` - `failed` - `cancelled`

Requirements: - Persist an event before attempting delivery. - Persist
endpoint-specific delivery state separately from the event. - Recover
queued work after app process restart or device reboot where Android
permits. - Allow users to inspect and clear queued events. - Apply
configurable retention limits for delivered and failed events. - Provide
storage safeguards so the queue cannot grow without bound. - Warn users
when storage limits or OS restrictions prevent normal processing.

### FR-09: Duplicate Handling

-   Generate a stable local event ID for each received event.
-   Use a local deduplication strategy based on available message
    metadata and a short configurable time window.
-   Avoid promising exactly-once delivery: HTTP retries can cause the
    same event to reach an endpoint more than once.
-   Document that receivers should deduplicate using `event_id` and make
    business operations idempotent.
-   Never silently drop an event solely because a webhook request timed
    out; the endpoint may have accepted the request before the timeout.

### FR-10: Offline Behavior

-   Queue events locally when no network is available.
-   Resume delivery when connectivity returns, subject to Android
    background execution limits.
-   Display pending queue count and last successful delivery.
-   Apply exponential backoff with jitter.
-   Allow users to pause and resume an endpoint.
-   Do not guarantee real-time delivery while offline or while Android
    restricts background work.

### FR-11: Local Configuration

-   Save endpoint and routing configuration on the device.
-   Store secrets in secure storage, separate from ordinary
    configuration records where possible.
-   Do not include tokens in configuration exports by default.
-   Warn users before importing configurations containing URLs or rules
    that may transmit data.
-   Validate imported configuration.
-   Provide a "Delete all local data" action with confirmation.

### FR-12: Webhook Test

-   Send a synthetic event that is clearly marked as a test.
-   Never use a real SMS body for a test request.
-   Display request outcome, response code, elapsed time, and a redacted
    error.
-   Avoid displaying or saving secrets.
-   A test success proves only that the endpoint returned an accepted
    HTTP response.

### FR-13: Developer and Open-Source Attribution

Display in About and README: - **Wisfy SMS** - **Developed by Shahria
Nafiz Sajjad** - Project source repository, once selected. - License and
third-party notices.

Do not invent a repository URL. Keep the repository URL in a project
constant or configuration file until the real repository is known.

------------------------------------------------------------------------

## 7. Security and Privacy Requirements

### 7.1 Endpoint validation

Because users can configure arbitrary URLs, the application must protect
against accidental or malicious data exfiltration.

-   Require HTTPS by default.
-   Block cleartext HTTP by default; if a development-only exception is
    provided, make it explicit and warn users.
-   Reject malformed URLs and unsupported schemes.
-   Reject embedded username/password credentials in URLs.
-   Do not silently follow redirects to a different host.
-   Warn users before enabling an endpoint.
-   Show the destination host clearly before the user enables
    forwarding.
-   Never expose the webhook token in logs, screenshots generated by the
    app, or diagnostics.
-   Do not include secrets in crash reports because no remote crash
    reporting is required.

### 7.2 Data minimization

-   Keep message content on-device unless the user has explicitly
    configured forwarding.
-   Provide body-redaction and parsed-fields-only modes.
-   Keep local logs for a configurable duration.
-   Allow deletion of individual events and all local data.
-   Do not collect advertising identifiers or device hardware
    identifiers.
-   Do not transmit analytics or telemetry to Wisfy.

### 7.3 Authentication limitations

Bearer tokens and custom headers are suitable for many integrations, but
a static token stored on a phone can be extracted from a compromised
device. Explain this limitation to users.

If HMAC request signing is implemented, specify the canonical signing
format, timestamp handling, replay window, and receiver verification
procedure in `docs/webhook-api.md`. Do not claim HMAC support until it
is implemented and tested.

### 7.4 Payment safety

An SMS is not authoritative proof of payment. Sender labels can be
spoofed in some contexts, message parsing can fail, and notifications
may be delayed or incomplete.

-   Mark extracted payment information as `unverified`.
-   Do not create a payment-success state inside Wisfy SMS.
-   Receiving applications must validate events and verify transaction
    status through an authoritative source where available.
-   Deduplicate payment fulfillment on the receiving server.
-   Never let an untrusted SMS alone trigger irreversible fulfillment
    without the receiving system's own validation policy.

------------------------------------------------------------------------

## 8. Android Permissions and Background Execution

-   Request only permissions needed for the features the user enables.
-   Explain why each permission is needed before requesting it.
-   Handle denial and revocation gracefully.
-   Follow the current Android version's rules for SMS permissions,
    background services, notifications, and battery restrictions.
-   Do not use hidden APIs or permission bypasses.
-   If a foreground service is required for a particular implementation,
    use the correct service type and persistent notification required by
    the target Android version.
-   Use WorkManager for suitable deferred/retry tasks; do not rely on it
    as a guaranteed immediate execution mechanism.
-   Test on multiple Android versions and at least one device with
    aggressive battery management.
-   Google Play distribution may restrict SMS-related permissions.
    Before Play Store release, review current Play policy and consider
    distribution options appropriate for the app's legitimate use case.

------------------------------------------------------------------------

## 9. Provider Parsers

The core application must be provider-independent. Parsers are optional
modules that convert known SMS formats into normalized fields.

Suggested interface:

``` text
SmsParser
- parserId
- supportedSenderPatterns
- canParse(message)
- parse(message) -> ParsedSmsResult
```

Requirements: - Include tests using synthetic, non-sensitive sample
messages. - Keep raw message forwarding separate from parsing. - Do not
claim a parser supports bKash, Nagad, Rocket, Upay, or any other
provider until its actual formats have been tested. - Support unknown
senders using the generic event schema. - Allow users to route by sender
even when no parser is available. - Avoid collecting real users' payment
SMS as test fixtures without informed consent and redaction.

------------------------------------------------------------------------

## 10. Proposed Repository Structure

``` text
wisfy-sms/
├── android/
├── lib/
│   ├── app/
│   ├── core/
│   │   ├── database/
│   │   ├── networking/
│   │   ├── security/
│   │   └── theme/
│   ├── features/
│   │   ├── onboarding/
│   │   ├── permissions/
│   │   ├── dashboard/
│   │   ├── endpoints/
│   │   ├── routing/
│   │   ├── queue/
│   │   ├── logs/
│   │   └── settings/
│   └── main.dart
├── docs/
│   ├── architecture.md
│   ├── webhook-api.md
│   ├── permissions.md
│   ├── privacy.md
│   ├── development.md
│   └── release.md
├── test/
├── .gitignore
├── analysis_options.yaml
├── CONTRIBUTING.md
├── LICENSE
├── README.md
├── Wisfy-sms.md
└── task.md
```

The implementation may adjust this structure if the selected
Flutter/Android architecture requires it. Do not create placeholder
files that falsely imply a feature is implemented.

------------------------------------------------------------------------

## 11. Documentation Requirements

### README.md

Must explain the project, supported platform, privacy model,
installation/build process, basic setup, developer attribution, license,
and links to detailed docs.

### docs/webhook-api.md

Must document: - Event schema and versioning. - Request headers. -
Authentication options actually implemented. - Sample event payloads. -
Test-event behavior. - HTTP success/failure handling. - Retry
behavior. - Duplicate delivery and idempotency. - Privacy/redaction
options. - Example receiver in at least one simple backend stack.

### docs/permissions.md

Explain permissions, Android background behavior, limitations, and
troubleshooting.

### docs/privacy.md

Explain what data remains local, what is sent to the configured
endpoint, data retention, secret handling, and the absence of
Wisfy-hosted telemetry.

### docs/development.md

Explain toolchain setup, local builds, testing, and how to add a parser.

------------------------------------------------------------------------

## 12. Testing Requirements

### Unit tests

-   URL validation.
-   Payload serialization and schema version.
-   Parser behavior and malformed messages.
-   Filtering and routing rules.
-   Retry/backoff calculations.
-   Redaction of sensitive fields.
-   Event ID and deduplication behavior.

### Integration tests

-   Successful HTTP 2xx delivery.
-   HTTP 4xx and 5xx handling.
-   Timeout after receiver acceptance.
-   Offline queue and subsequent delivery.
-   Multiple endpoints with independent states.
-   Token masking in logs.
-   Redirect rejection or safe redirect handling.
-   Synthetic webhook test event.

### Device tests

-   Permission granted/denied/revoked.
-   Incoming SMS reception on supported Android versions.
-   App process termination and recovery.
-   Device reboot recovery where permitted.
-   Battery optimization behavior.
-   Network switching between Wi-Fi and mobile data.
-   Local data deletion.

Use mock SMS and test endpoints. Never use live payment messages or real
financial transactions as routine automated test fixtures.

------------------------------------------------------------------------

## 13. Implementation Plan

### Phase 1 --- Foundation

-   [ ] Inspect repository and existing code before making changes.
-   [ ] Set up Flutter project and Android configuration.
-   [ ] Implement design tokens, navigation, and theme.
-   [ ] Build onboarding and permission explanation screens.
-   [ ] Add About screen and developer attribution.
-   [ ] Add project README and license.

### Phase 2 --- Local Storage and Endpoints

-   [ ] Implement local database schema and migrations.
-   [ ] Implement secure credential storage.
-   [ ] Add endpoint CRUD.
-   [ ] Add endpoint validation and HTTPS enforcement.
-   [ ] Add routing/filter configuration.
-   [ ] Add synthetic Test Webhook flow.

### Phase 3 --- SMS and Event Pipeline

-   [ ] Implement Android SMS receiver with explicit permission
    handling.
-   [ ] Normalize eligible messages into versioned events.
-   [ ] Implement optional parser interface.
-   [ ] Implement local deduplication.
-   [ ] Add privacy filters and body-redaction settings.
-   [ ] Persist events and endpoint-specific delivery records.

### Phase 4 --- Webhook Delivery

-   [ ] Implement HTTPS JSON POST.
-   [ ] Add configurable authentication and headers.
-   [ ] Add bounded request timeouts.
-   [ ] Implement retry policy and backoff with jitter.
-   [ ] Add offline queue recovery.
-   [ ] Add delivery logs and manual retry.
-   [ ] Ensure credentials and sensitive message data are redacted from
    logs.

### Phase 5 --- UI Completion

-   [ ] Complete dashboard counters and activity list.
-   [ ] Complete endpoint details and routing screens.
-   [ ] Complete queue and delivery log filters.
-   [ ] Add local retention and delete-all-data controls.
-   [ ] Add loading, empty, error, and permission-denied states.
-   [ ] Verify accessibility, text scaling, and responsive layouts.

### Phase 6 --- Verification and Release

-   [ ] Run unit, integration, and device tests.
-   [ ] Verify no required network request goes to Wisfy infrastructure.
-   [ ] Inspect dependencies and their licenses.
-   [ ] Complete webhook, permissions, privacy, and development docs.
-   [ ] Verify README build instructions from a clean checkout.
-   [ ] Produce a debug build and a signed release build using
    user-provided signing credentials.
-   [ ] Review Android distribution policy before publishing.
-   [ ] Confirm that all checked tasks correspond to tested, working
    functionality.

------------------------------------------------------------------------

## 14. Definition of Done

The first release is complete only when:

1.  The app can run without a Wisfy account or Wisfy-hosted backend.
2.  Endpoint configuration is stored locally.
3.  An eligible SMS can be received with permission and converted to a
    documented event.
4.  A configured endpoint receives an HTTPS JSON POST.
5.  Delivery outcomes are recorded locally.
6.  Failed events are retried according to the documented policy.
7.  Multiple endpoints have independent settings and delivery states.
8.  Sensitive data and credentials are not exposed in logs.
9.  Users can inspect and delete local records.
10. The README and webhook documentation match the actual
    implementation.
11. Developer attribution reads **"Developed by Shahria Nafiz Sajjad."**
12. No UI element claims that payment has been verified merely because
    an SMS was received or a webhook returned 2xx.

------------------------------------------------------------------------

## 15. AI Coder Instructions

Before coding:

1.  Inspect the complete existing repository.
2.  Read this PRD and any existing project documentation.
3.  Create `/task.md` with a step-by-step implementation checklist
    derived from this document.
4.  Split tasks into small, verifiable units and include acceptance
    criteria.
5.  Identify Android permission and background-execution constraints
    early.
6.  Choose maintained dependencies and verify their current APIs.
7.  Keep the architecture backend-free.
8.  Do not add Supabase, Firebase, analytics, remote configuration, or a
    hosted relay.
9.  Implement actual functionality; do not substitute mock screens for
    working SMS reception or webhook delivery.
10. Keep the UI aligned with the supplied Wisfy SMS reference.
11. Add tests for core delivery and security behavior.
12. Update task checkboxes only after implementation and verification.
13. Do not claim any feature is complete unless it has been tested.
14. If a requirement is blocked by Android or distribution policy,
    document the limitation and propose a compliant implementation
    instead of bypassing the restriction.

**Final product statement:** Wisfy SMS is an open-source, local-first
Android SMS-to-webhook application. Users control their endpoints and
data flow. No Wisfy backend is required.

---

## 16. Website and External Application Integration

This section is a core requirement: a developer must be able to connect Wisfy SMS to an existing website or application and make that website receive SMS events correctly, without using a Wisfy-hosted backend.

### 16.1 Integration model

Wisfy SMS is the Android-side sender. The destination website or application must expose an HTTP(S) endpoint that accepts the configured JSON webhook. The connection is:

```text
Android phone running Wisfy SMS
        |
        | HTTPS POST (JSON event)
        v
Website / application's webhook endpoint
        |
        v
Validate request -> store event -> run application-specific logic
```

Wisfy SMS does not create an endpoint on the website, log into the website, or automatically understand every website's private API. The website developer must provide or configure a receiving endpoint that matches the documented Wisfy SMS payload. If the website expects a different format, the receiver should transform the incoming Wisfy event into that format.

### 16.2 Website connection workflow

The app and its documentation must support this user workflow:

1. In the target website, create or enable an endpoint such as `https://your-domain.example/api/webhooks/wisfy-sms`.
2. Generate a secret/token on the receiving website, if its implementation supports authentication.
3. In Wisfy SMS, open **Add Webhook**, enter an endpoint name and the full HTTPS URL, and configure the same authentication method/token expected by the receiver.
4. Select event types and SMS sender filters. Do not enable forwarding of all messages by default.
5. Press **Test Webhook**. Wisfy SMS sends a synthetic `wisfy.test` event with no real SMS content.
6. The website verifies the request and returns a 2xx response only after it has accepted the event according to its documented behavior.
7. Wisfy SMS displays the HTTP result, response time, and redacted error if the test fails.
8. Enable forwarding only after the test succeeds. Send a harmless test SMS or use a synthetic event to verify the full flow.
9. Check both Wisfy SMS delivery logs and the receiving website's logs.

The app must clearly explain that an HTTP 2xx response means the endpoint accepted the HTTP request; it does not mean a payment has been verified.

### 16.3 Required endpoint contract

The receiving endpoint should:

- Accept HTTPS POST requests with `Content-Type: application/json`.
- Support the documented Wisfy SMS schema version.
- Authenticate the request when authentication is configured.
- Validate input size, field types, event name, and schema version.
- Treat `event_id` as an idempotency key and avoid duplicate business actions.
- Persist/queue the accepted event before returning 2xx when durable processing is required.
- Return an appropriate non-2xx status when it cannot accept the event.
- Never treat the raw SMS alone as authoritative proof of payment.
- Avoid logging tokens or unnecessary sensitive SMS contents.

Suggested request headers:

```http
Content-Type: application/json
X-Wisfy-Event: sms.received
X-Wisfy-Event-Id: evt_example_001
X-Wisfy-Schema-Version: 1.0
Authorization: Bearer <receiver-configured-token>
```

The `Authorization` header is optional and should be sent only when the user configures it. The receiver and Wisfy SMS must use the same token. Never put production tokens in source code, screenshots, README examples, or logs.

### 16.4 Website-side sample receiver (Node.js / Express)

The repository documentation must include a small, runnable example receiver so developers can verify integration. This example demonstrates request validation and idempotency only; it is not a production payment verifier. Production implementations should use a durable database/queue and their framework's security recommendations.

```js
import express from "express";
import crypto from "node:crypto";

const app = express();
app.use(express.json({ limit: "64kb" }));

const webhookToken = process.env.WISFY_WEBHOOK_TOKEN;
if (!webhookToken) {
  throw new Error("Set WISFY_WEBHOOK_TOKEN in the environment");
}

// Demo only. Use a persistent database with a unique constraint on event_id
// in production; this in-memory set is cleared when the process restarts.
const seenEventIds = new Set();

app.post("/api/webhooks/wisfy-sms", (req, res) => {
  const auth = req.get("authorization") || "";
  const suppliedToken = auth.startsWith("Bearer ") ? auth.slice(7) : "";
  const a = Buffer.from(suppliedToken);
  const b = Buffer.from(webhookToken);
  const tokenMatches = a.length === b.length && crypto.timingSafeEqual(a, b);

  if (!tokenMatches) {
    return res.status(401).json({ success: false, error: "unauthorized" });
  }

  const event = req.body;
  if (!event || typeof event.event_id !== "string" ||
      typeof event.event !== "string" ||
      typeof event.schema_version !== "string") {
    return res.status(400).json({ success: false, error: "invalid_event" });
  }

  if (event.event !== "sms.received" && event.event !== "wisfy.test") {
    return res.status(400).json({ success: false, error: "unsupported_event" });
  }

  if (seenEventIds.has(event.event_id)) {
    // Duplicate received: acknowledge without repeating the business action.
    return res.status(200).json({ success: true, duplicate: true });
  }
  seenEventIds.add(event.event_id);

  // TODO: Persist event durably before returning 2xx in production.
  // Do not mark a payment as successful based only on the SMS content.
  console.log("Wisfy event accepted", {
    event_id: event.event_id,
    event: event.event,
    schema_version: event.schema_version
  });

  return res.status(200).json({ success: true, received: true });
});

app.listen(process.env.PORT || 3000, () => {
  console.log("Wisfy SMS example receiver is running");
});
```

Example environment setup:

```dotenv
WISFY_WEBHOOK_TOKEN=replace-with-a-long-random-secret
PORT=3000
```

Do not commit the environment file containing the real secret. Serve the endpoint behind HTTPS in production. The in-memory deduplication in this short example is for demonstration only; production must persist event IDs and event processing status in a database with a unique constraint.

### 16.5 Connecting different website technologies

`docs/webhook-api.md` must explain how to create a receiver in common stacks, or provide clear generic guidance for developers to adapt:

- Node.js / Express or Next.js Route Handler
- PHP / Laravel
- Python / Django or FastAPI
- Other frameworks that can receive HTTP POST JSON
- Automation platforms that accept authenticated webhooks

Do not claim that a website is compatible merely because it uses one of these stacks. The actual endpoint must be deployed, reachable from the phone's network, accept the configured authentication and payload, and return a suitable HTTP status.

For a platform with a proprietary API or a different expected payload, developers may need a website-side adapter, plugin, or automation workflow. Wisfy SMS should not contain hard-coded credentials for external websites.

### 16.6 Connection diagnostics

For every endpoint, provide actionable status/error messages, such as:

- **Connected / HTTP 2xx:** endpoint accepted the request.
- **Unauthorized / HTTP 401 or 403:** check the configured token or receiver permissions.
- **Bad Request / HTTP 400 or 422:** check the receiver's required payload fields and schema version.
- **Not Found / HTTP 404:** check the endpoint path and deployment routing.
- **Rate Limited / HTTP 429:** wait according to `Retry-After` when provided.
- **Server Error / HTTP 5xx:** the receiving service failed; retry according to policy.
- **Timeout / Network Error:** the result may be ambiguous; keep the same event ID on retry because the receiver may already have accepted the request.
- **TLS/Certificate Error:** verify the domain's HTTPS certificate; never silently disable certificate validation.

Display the hostname and a redacted error, never the token or full sensitive payload.

### 16.7 Webhook documentation deliverables

The AI coder must implement and maintain:

- `docs/webhook-api.md` — schema, headers, authentication, request/response examples, test events, retry semantics, duplicate delivery, and privacy behavior.
- `docs/website-integration.md` — step-by-step website connection guide, deployment checklist, endpoint diagnostics, and sample receiver.
- `examples/webhook-receiver-node/` — a clearly labelled sample receiver and `.env.example` without real credentials, if the repository layout supports examples.
- README links to both guides.

The guides must match the actual implementation. Do not document a header, authentication method, event type, or feature that has not been implemented and tested.

### 16.8 Acceptance criteria for website integration

- A developer can create a receiver using the documentation without a Wisfy account or Wisfy backend.
- Wisfy SMS can test an endpoint using a synthetic event without sending a real SMS body.
- A correctly configured receiver returns 2xx and the app shows the test result.
- Wrong credentials produce a clear 401/403-style failure and do not expose the secret.
- Invalid payloads are rejected by the sample receiver.
- Retries preserve the same event ID, and the receiver demonstrates duplicate-safe handling.
- The docs explain that payment verification belongs to the receiving application.
- The integration works with any compatible user-owned HTTPS endpoint, not only a named Wisfy project.

---

## 17. Updated AI Coder Instructions

In addition to all requirements above, the AI coder must:

1. Implement the **website connection capability**, not just a screen for entering a URL.
2. Implement endpoint configuration, a synthetic Test Webhook request, real SMS event delivery, response handling, retries, logs, and secure credential storage.
3. Provide a complete website integration guide and a working sample receiver.
4. Verify the flow end-to-end against a local/mock receiver and, where possible, a deployed test endpoint.
5. Keep all requests direct from the Android app to the user-configured endpoint; do not add a Wisfy relay or backend.
6. Mark checklist tasks complete only after code and tests confirm them.

**Final integration goal:** a developer can add Wisfy SMS support to their own website by implementing the documented receiver endpoint, then configure that URL in the app and successfully test the connection.

---

## 18. Duplicate SMS Prevention and Payment-SMS Authenticity Rules

### 18.1 Goal

Wisfy SMS must prevent the same incoming SMS or the same payment transaction from being forwarded multiple times, and it must not label an SMS as a confirmed payment merely because its text mentions bKash, Nagad, Rocket, or Upay. This is especially important for merchant websites that use SMS notifications to update orders.

**Important limitation:** an Android app that only reads SMS cannot conclusively prove that a payment really occurred. Sender names and SMS text can be spoofed, altered, or imitated in some circumstances. Wisfy SMS must report what it observed and parse cautiously; the receiving website remains responsible for authoritative payment verification.

### 18.2 Two-layer deduplication

Implement both layers below. They solve different problems and are both required.

#### Layer A — Incoming-message deduplication

- Assign each received SMS a stable local event record before attempting webhook delivery.
- Build a normalized message fingerprint from the SMS sender/address, normalized body, subscription/SIM identifier where available, and the received timestamp rounded to an appropriate small time bucket.
- Store fingerprints in a persistent local database with a unique constraint. Do not rely only on in-memory variables, because the app may restart or the phone may reboot.
- If Android delivers the same SMS broadcast more than once, or the same message is encountered again during processing, mark it as a duplicate and do not create another forward event.
- Do not use body text alone as the deduplication key: two legitimate payments can have similar message text.
- Keep the original received time and the normalized fingerprint separately for diagnostics. Avoid logging full message bodies unless the user explicitly enables a privacy-conscious debug option.

#### Layer B — Payment-transaction deduplication

- Parse a transaction reference/transaction ID (for example, `TrxID`, `TxnID`, `Transaction ID`, or a provider-specific equivalent) only when the SMS matches a configured provider template.
- Normalize the transaction ID by trimming whitespace and applying safe case normalization only where appropriate. Do not remove meaningful characters.
- Use a unique key composed of provider + transaction ID; include the receiving merchant/account scope only if the integration contract explicitly requires it. A transaction ID already processed for that provider must not trigger a second payment/order update.
- If a provider notification has no transaction ID, do not silently assume it is unique. Apply a conservative fallback fingerprint and mark the event as `transaction_reference_missing` so the receiver can decide whether manual review is needed.
- Retries of the same webhook event must preserve the original `event_id` and idempotency key. Never create a new event ID just because a delivery attempt failed.
- Document that deduplication must also be enforced by the receiving website/database using a persistent unique constraint. Client-side deduplication alone is not sufficient for financial workflows.

### 18.3 Provider profiles: bKash, Nagad, Rocket, and Upay

Create configurable provider profiles for:

- bKash
- Nagad
- Rocket
- Upay

Each profile must define a configurable list of trusted sender addresses/IDs, supported SMS templates, required fields, amount/currency parsing, transaction-reference extraction, and validation rules. Sender IDs and message templates may differ by operator, account type, service version, or notification language, so do not hardcode a single sender ID or assume one permanent message format.

For each provider profile:

- Only classify a message as a provider payment notification if both the sender matches a user/admin-configured trusted sender entry and the body matches a supported provider template with required fields.
- Extract fields only from the known template structure. Do not classify a message as a successful payment just because it contains keywords such as `received`, `payment`, `Tk`, `BDT`, `bKash`, `Nagad`, `Rocket`, or `Upay`.
- Validate that amount is a positive, parseable number; currency and transaction reference follow the configured rules; and required fields are present.
- Where supported by the message template, distinguish received-money notifications from sent-money, cash-out, charge, refund, failed, pending, reversed, or informational notifications. Only the explicitly configured successful incoming-payment template may be emitted as `payment_notification_candidate`.
- Never infer success from a balance change alone or from a message that lacks an explicit success/received status.
- If a message does not match a supported template exactly enough to parse safely, classify it as `unknown` or `needs_review`; do not forward it as a verified payment.
- Provide a test-message/template preview so an administrator can validate provider profiles without using a real payment.
- Keep template configuration versioned. Template changes must include test fixtures for valid, invalid, duplicate, malformed, outgoing-payment, failed, reversed, and lookalike/fake messages.

### 18.4 Do not trust the sender ID alone

SMS sender labels are not cryptographic proof of origin. Therefore:

- A trusted sender allowlist is a filtering aid, not proof that the transaction is genuine.
- Do not provide a green `Verified Payment` status based solely on sender ID plus parsed SMS text.
- Use explicit statuses such as `received_from_android`, `parsed_candidate`, `duplicate`, `unknown_sender`, `template_mismatch`, `needs_review`, and `server_verified`.
- The app must never fabricate provider verification or claim to query a provider API unless that API integration genuinely exists and succeeds.
- If the receiving website supports official provider APIs, a merchant statement/reconciliation source, or a payment-gateway verification endpoint, it should verify the transaction server-side before fulfilling an order.
- If no authoritative verification source is available, the website must show the result as an unverified SMS candidate and should not automatically release goods or mark a high-risk order as paid solely from SMS text.

### 18.5 Webhook event schema additions

Add the following fields to the webhook payload where applicable:

```json
{
  "event_id": "stable-unique-event-id",
  "event_type": "sms.payment_candidate",
  "schema_version": "1.1",
  "deduplication": {
    "message_duplicate": false,
    "transaction_duplicate": false,
    "idempotency_key": "provider:transaction-reference-or-fingerprint"
  },
  "payment_candidate": {
    "provider": "bkash",
    "status": "parsed_candidate",
    "amount": "1250.00",
    "currency": "BDT",
    "transaction_id": "EXAMPLE123",
    "sender_match": true,
    "template_match": true,
    "server_verified": false
  }
}
```

This is an illustrative schema only. Use the actual extracted values, never the example values. Omit unavailable fields or set them to `null` according to the documented schema. Do not send `server_verified: true` unless a real authoritative verification step has succeeded. The receiver must treat all fields derived from SMS as untrusted input.

If duplicate events are detected, the default behavior should be to suppress a second financial webhook. If diagnostic forwarding of duplicates is supported, it must use a separate non-financial event type such as `sms.duplicate_detected`, and the receiving website must never process that diagnostic event as a payment.

### 18.6 Website receiver requirements

The sample receiver and integration guide must demonstrate all of the following:

1. Validate webhook authentication and the request schema.
2. Use a database transaction and persistent unique constraints for `event_id` and provider + transaction ID.
3. Return a successful idempotent response for an already-processed event without repeating the order update.
4. Validate amount, currency, provider, transaction ID, merchant/account mapping, and expected order reference where available.
5. Never trust a client-provided `server_verified` field. Verification status must be computed by the website's own trusted server-side verification flow.
6. Separate `SMS parsed`, `duplicate`, `needs review`, and `server verified` states in logs/admin UI.
7. Never automatically mark an order paid when required transaction data is missing, the message template is unknown, the transaction ID is already used, or authoritative verification fails.
8. Use a persistent database in production; the in-memory deduplication example is demonstration-only.

### 18.7 Acceptance tests

The AI coder must create automated tests/fixtures proving:

- The same SMS broadcast received twice creates only one message event.
- A retry after timeout uses the same event ID and idempotency key.
- The same provider transaction ID arriving in two different SMS messages cannot cause two payment/order updates.
- Similar-looking SMS bodies with different valid transaction IDs are not incorrectly deduplicated as the same payment.
- A message from an untrusted sender is not classified as a payment candidate.
- A message from a trusted sender with an invalid or unknown template is not classified as a successful incoming payment.
- Outgoing, failed, pending, reversed, refund, and informational SMS messages are not treated as successful incoming payments.
- Missing/invalid amount or transaction reference is flagged and never silently marked verified.
- bKash, Nagad, Rocket, and Upay each have independent configurable profiles and test fixtures.
- A receiver that gets the same event twice performs the financial side effect only once.
- App restart/reboot does not erase the local deduplication history while its configured retention period is active.

### 18.8 Privacy and retention

- Store only the minimum SMS data required for forwarding, deduplication, and user-visible troubleshooting.
- Make retention settings configurable and document how to clear local logs safely.
- Never place full SMS content, transaction references, webhook tokens, or personal data in ordinary application logs or crash reports.
- Redact sensitive values in diagnostics and screenshots.

### 18.9 AI coder implementation instruction

Treat this as a financial-safety requirement, not an optional enhancement. Implement persistent two-layer deduplication, configurable bKash/Nagad/Rocket/Upay parsing profiles, explicit unverified statuses, secure webhook idempotency, and receiver-side database uniqueness. Do not promise that SMS alone can eliminate all fake-payment risk. Document that reliable payment confirmation requires an authoritative server-side verification or reconciliation source where available.
