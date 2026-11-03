# Website & External Application Integration Guide

**Product:** Wisfy SMS  
**Developed by Shahria Nafiz Sajjad**

This guide provides instructions for connecting any website, merchant application, e-commerce backend, or automation platform to Wisfy SMS.

---

## 1. Step-by-Step Connection Workflow

1. In your backend or website, create an endpoint:  
   `POST /api/webhooks/wisfy-sms`
2. Generate a secure random token (e.g., `wisfy_sec_...`).
3. In the Wisfy SMS app:
   - Go to **Endpoints** -> **+ Connect Webhook**.
   - Enter your HTTPS endpoint URL.
   - Enter the same Bearer token.
   - Specify sender filters if needed (e.g. `bKash, Nagad, Rocket, Upay`).
   - **Configure SIM Card Routing:**
     - Select `Both SIMs (Default)` if both SIM cards receive business payments.
     - Select `SIM 1 Only` or `SIM 2 Only` to isolate merchant messages from personal SIM messages.
   - Tap **Test Webhook**.
4. Wisfy SMS transmits a synthetic `wisfy.test` event.
5. Once your server returns HTTP 200, tap **Save & Activate Webhook**.

---

## 2. Generate Your Website Receiver in 10 Seconds with AI

Instead of manually coding the receiver from scratch, you can copy our pre-engineered prompt into **Claude (3.7 / 3.5 Sonnet)**, **OpenAI ChatGPT**, or **Codex**:

👉 Open [`docs/ai-integration-prompt.md`](./ai-integration-prompt.md), copy the markdown block, and paste it into your AI assistant. The AI will output complete Next.js / Express / Laravel / FastAPI code tailored to your exact database schema.

---

## 2. Receiver Implementation Checklist

- [x] Reject requests with missing or invalid Authorization tokens (HTTP 401).
- [x] Validate `event_id`, `event`, and `schema_version`.
- [x] Check for duplicate `event_id` in your database.
- [x] Return HTTP 200 within 5 seconds.
- [x] Never log private SMS bodies or sensitive keys.
