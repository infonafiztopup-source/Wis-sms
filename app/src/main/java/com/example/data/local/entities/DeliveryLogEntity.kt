package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "delivery_logs",
    indices = [
        Index(value = ["eventId"]),
        Index(value = ["endpointId"]),
        Index(value = ["status"])
    ]
)
data class DeliveryLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: String,
    val endpointId: Long,
    val endpointName: String,
    val endpointUrl: String,
    val status: String, // "delivered", "queued", "sending", "failed", "retry_scheduled", "filtered"
    val attemptCount: Int = 1,
    val httpStatusCode: Int? = null,
    val latencyMs: Long = 0,
    val responseBodySnippet: String? = null,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null
)
