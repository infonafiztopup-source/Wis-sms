package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "processed_message_fingerprints",
    indices = [Index(value = ["fingerprint"], unique = true)]
)
data class ProcessedMessageFingerprint(
    @PrimaryKey
    val fingerprint: String,
    val eventId: String,
    val sender: String,
    val receivedAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)
