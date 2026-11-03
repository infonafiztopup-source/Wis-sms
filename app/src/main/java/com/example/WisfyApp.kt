package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.repository.WisfyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WisfyApp : Application() {
    lateinit var repository: WisfyRepository
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = WisfyRepository(this)
        createNotificationChannels()

        // Ensure foreground daemon is active if enabled in settings
        appScope.launch {
            try {
                val settings = repository.getSettings()
                if (settings.backgroundServiceActive) {
                    com.example.service.WisfyForegroundService.start(this@WisfyApp)
                }
            } catch (_: Exception) {}
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_GATEWAY,
                "Wisfy SMS Gateway Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows status of background SMS listening and webhook forwarding"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_GATEWAY = "wisfy_sms_gateway_channel"
        lateinit var instance: WisfyApp
            private set
    }
}
