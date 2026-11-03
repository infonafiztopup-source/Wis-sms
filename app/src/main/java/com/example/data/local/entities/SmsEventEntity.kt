package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_events")
data class SmsEventEntity(
    @PrimaryKey
    val eventId: String,
    val eventType: String = "sms.received", // "sms.payment_candidate", "sms.duplicate_detected", "sms.received"
    val sender: String,
    val rawBody: String,
    val sanitizedBody: String,
    val receivedAt: Long,
    val schemaVersion: String = "1.1",
    val deviceId: String,
    val simSlot: Int = 0, // 0 = Unknown/Any, 1 = SIM 1, 2 = SIM 2
    val provider: String = "unknown",
    val transactionId: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val status: String = "unverified", // "parsed_candidate", "duplicate", "needs_review", "unverified"
    val isTest: Boolean = false,
    val fingerprint: String = "",
    val idempotencyKey: String = "",
    val isMessageDuplicate: Boolean = false,
    val isTransactionDuplicate: Boolean = false,
    val senderMatch: Boolean = false,
    val templateMatch: Boolean = false,
    val serverVerified: Boolean = false,
    val reviewReason: String? = null
)
