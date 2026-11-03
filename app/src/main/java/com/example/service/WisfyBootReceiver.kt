package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.WisfyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Automatically starts the Wisfy Foreground Service daemon on device boot or app update
 * if the user has background service enabled in settings.
 */
class WisfyBootReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            receiverScope.launch {
                try {
                    val repository = WisfyApp.instance.repository
                    val settings = repository.getSettings()
                    if (settings.backgroundServiceActive) {
                        WisfyForegroundService.start(context.applicationContext)
                    }
                } catch (_: Exception) {
                    // Ignore errors during boot
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
