package com.example.domain.model

data class ParsedSmsResult(
    val provider: String = "unknown",
    val transactionId: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val status: String = "unverified", // "parsed_candidate", "duplicate", "unknown_sender", "template_mismatch", "needs_review", "outgoing_filtered"
    val isPaymentCandidate: Boolean = false,
    val senderMatch: Boolean = false,
    val templateMatch: Boolean = false,
    val isOutgoingOrReversal: Boolean = false,
    val reviewReason: String? = null,
    val confidence: Float = 0.0f
)
