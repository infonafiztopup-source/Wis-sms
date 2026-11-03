package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "webhook_endpoints")
data class WebhookEndpointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val url: String,
    val httpMethod: String = "POST",
    val authType: String = "Bearer", // "Bearer", "Custom", "None"
    val authToken: String = "",
    val customHeadersJson: String = "{}",
    val senderFilter: String = "", // Comma-separated sender keywords/numbers or empty for all allowed
    val simSlot: Int = 0, // 0 = Both SIMs (Default), 1 = SIM 1 Only, 2 = SIM 2 Only
    val eventTypesJson: String = "[\"sms.received\"]",
    val timeoutSec: Int = 10,
    val maxRetries: Int = 3,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastTestedAt: Long? = null,
    val lastTestSuccess: Boolean? = null,
    val lastTestStatus: Int? = null,
    val lastTestLatencyMs: Long? = null,
    val lastTestMessage: String? = null
)
