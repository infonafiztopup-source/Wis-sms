package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "processed_transactions",
    indices = [Index(value = ["transactionKey"], unique = true)]
)
data class ProcessedTransactionEntity(
    @PrimaryKey
    val transactionKey: String, // e.g. "bkash:9K8A7L6M2P" or "nagad:7X89QPL2"
    val provider: String,
    val transactionId: String,
    val amount: Double?,
    val currency: String?,
    val originalEventId: String,
    val firstProcessedAt: Long = System.currentTimeMillis()
)
