package com.example.domain.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WisfyPayload(
    val event: String,
    val eventType: String = "sms.payment_candidate", // "sms.payment_candidate", "sms.duplicate_detected", "sms.received"
    val eventId: String,
    val schemaVersion: String = "1.1",
    val deviceId: String,
    val receivedAtMs: Long,
    val sender: String,
    val body: String?, // null if raw body forwarding is disabled
    val provider: String = "unknown",
    val transactionId: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val status: String = "unverified",
    val idempotencyKey: String = "",
    val isMessageDuplicate: Boolean = false,
    val isTransactionDuplicate: Boolean = false,
    val senderMatch: Boolean = false,
    val templateMatch: Boolean = false,
    val serverVerified: Boolean = false,
    val simSlot: Int = 0
) {
    fun toJsonString(): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        val isoDate = isoFormat.format(Date(receivedAtMs))

        val root = JSONObject()
        root.put("event", event)
        root.put("event_type", eventType)
        root.put("event_id", eventId)
        root.put("schema_version", schemaVersion)
        root.put("device_id", deviceId)
        root.put("received_at", isoDate)
        root.put("sim_slot", if (simSlot > 0) simSlot else 1)

        // Deduplication Object per PRD Section 18.5
        val dedupObj = JSONObject()
        dedupObj.put("message_duplicate", isMessageDuplicate)
        dedupObj.put("transaction_duplicate", isTransactionDuplicate)
        dedupObj.put("idempotency_key", idempotencyKey.ifBlank { "$provider:${transactionId ?: eventId}" })
        root.put("deduplication", dedupObj)

        // Payment Candidate Object per PRD Section 18.5
        val candidateObj = JSONObject()
        candidateObj.put("provider", provider)
        candidateObj.put("status", status)
        candidateObj.put("amount", if (amount != null) String.format(Locale.US, "%.2f", amount) else JSONObject.NULL)
        candidateObj.put("currency", currency ?: JSONObject.NULL)
        candidateObj.put("transaction_id", transactionId ?: JSONObject.NULL)
        candidateObj.put("sender_match", senderMatch)
        candidateObj.put("template_match", templateMatch)
        candidateObj.put("server_verified", serverVerified)
        root.put("payment_candidate", candidateObj)

        // Message Object
        val messageObj = JSONObject()
        messageObj.put("sender", sender)
        if (body != null) {
            messageObj.put("body", body)
        } else {
            messageObj.put("body", JSONObject.NULL)
        }
        messageObj.put("sim_slot", if (simSlot > 0) simSlot else 1)
        messageObj.put("received_at", isoDate)
        root.put("message", messageObj)

        return root.toString(2)
    }

    companion object {
        fun createSyntheticTest(deviceId: String): WisfyPayload {
            val eventId = "evt_test_${System.currentTimeMillis()}"
            val testTrx = "TEST_PING_${System.currentTimeMillis() % 100000}"
            return WisfyPayload(
                event = "sms.received",
                eventType = "wisfy.test",
                eventId = eventId,
                schemaVersion = "1.1",
                deviceId = deviceId,
                receivedAtMs = System.currentTimeMillis(),
                sender = "WISFY_DIAGNOSTICS",
                body = "Wisfy SMS synthetic connectivity test verification",
                provider = "system_test",
                transactionId = testTrx,
                amount = 1.00,
                currency = "USD",
                status = "parsed_candidate",
                idempotencyKey = "system_test:$testTrx",
                isMessageDuplicate = false,
                isTransactionDuplicate = false,
                senderMatch = true,
                templateMatch = true,
                serverVerified = false
            )
        }
    }
}
