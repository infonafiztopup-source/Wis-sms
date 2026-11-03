package com.example.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.WisfyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WisfyForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isExplicitlyStopped = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isExplicitlyStopped = false
        // Start foreground immediately in onCreate to avoid background start exceptions on Android 12+
        promoteToForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            isExplicitlyStopped = true
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        isExplicitlyStopped = false
        promoteToForeground()

        // START_STICKY instructs Android to recreate the service if killed by OS
        return START_STICKY
    }

    private fun promoteToForeground() {
        try {
            val notification = createNotification()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (_: Exception) {
            // Guard against edge cases during process death
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // User swiped app away from recent apps list:
        // Schedule revival of the foreground daemon so background SMS processing never stops
        if (!isExplicitlyStopped) {
            scheduleServiceRevival(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // If destroyed unexpectedly (not via explicit ACTION_STOP), schedule immediate restart
        if (!isExplicitlyStopped) {
            serviceScope.launch {
                try {
                    val settings = WisfyApp.instance.repository.getSettings()
                    if (settings.backgroundServiceActive) {
                        scheduleServiceRevival(applicationContext)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun createNotification(
        title: String = "Wisfy SMS Gateway • Active",
        text: String = "Running in background • Ready to forward incoming SMS"
    ): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, WisfyApp.CHANNEL_ID_GATEWAY)
            .setContentTitle(title)
            .setContentText(text)
            .setSubText("Running in background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setShowWhen(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.service.START"
        const val ACTION_STOP = "com.example.service.STOP"

        fun updateStatus(context: Context, title: String, text: String) {
            try {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, WisfyApp.CHANNEL_ID_GATEWAY)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSubText("Running in background")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setShowWhen(true)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            } catch (_: Exception) {}
        }

        fun start(context: Context) {
            try {
                val intent = Intent(context, WisfyForegroundService::class.java).apply {
                    action = ACTION_START
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                // Ignore background start exceptions
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, WisfyForegroundService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            } catch (_: Exception) {}
        }

        fun scheduleServiceRevival(context: Context) {
            try {
                val restartServiceIntent = Intent(context, WisfyForegroundService::class.java).apply {
                    action = ACTION_START
                }
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getForegroundService(
                        context,
                        1002,
                        restartServiceIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getService(
                        context,
                        1002,
                        restartServiceIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000L,
                    pendingIntent
                )
            } catch (_: Exception) {}
        }
    }
}
