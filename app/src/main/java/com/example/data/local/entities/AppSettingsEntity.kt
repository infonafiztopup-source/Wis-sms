package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val maskSmsBodyInLogs: Boolean = true,
    val forwardRawMessageBody: Boolean = false,
    val autoFilterOtp: Boolean = true,
    val backgroundServiceActive: Boolean = true,
    val batteryOptimizationExempt: Boolean = false,
    val retryCount: Int = 3,
    val timeoutSeconds: Int = 10,
    val hasCompletedOnboarding: Boolean = false,
    val deviceId: String = "",
    val themeMode: String = "LIGHT" // "LIGHT", "DARK", "SYSTEM"
)
