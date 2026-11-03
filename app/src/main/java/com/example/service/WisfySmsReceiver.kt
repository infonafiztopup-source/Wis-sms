package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Telephony
import com.example.WisfyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WisfySmsReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION ||
            action == "android.provider.Telephony.SMS_DELIVER"
        ) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) return

            val sender = messages[0].displayOriginatingAddress ?: "Unknown"
            val timestamp = messages[0].timestampMillis

            // Extract SIM slot index from intent extras (supporting multi-SIM devices)
            val subId = intent.getIntExtra("subscription", -1)
            val slotExtra = intent.getIntExtra("slot", -1)
            val simIdExtra = intent.getIntExtra("simId", -1)
            val phoneExtra = intent.getIntExtra("phone", -1)
            val simSlot = when {
                slotExtra >= 0 -> slotExtra + 1
                simIdExtra >= 0 -> simIdExtra + 1
                phoneExtra >= 0 -> phoneExtra + 1
                subId >= 0 -> (subId % 2) + 1
                else -> 1
            }

            // Reconstruct full multipart SMS body
            val fullBody = buildString {
                for (msg in messages) {
                    append(msg.displayMessageBody ?: "")
                }
            }

            if (fullBody.isNotBlank()) {
                val pendingResult = goAsync()

                // Acquire partial wake-lock so CPU does not go to sleep while transmitting webhook
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                val wakeLock = powerManager?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "Wisfy::SmsProcessingWakeLock"
                )?.apply {
                    setReferenceCounted(false)
                    acquire(60_000L) // 60 seconds maximum timeout
                }

                receiverScope.launch {
                    try {
                        // Ensure foreground daemon is running
                        val repository = WisfyApp.instance.repository
                        val settings = repository.getSettings()
                        if (settings.backgroundServiceActive) {
                            WisfyForegroundService.start(context.applicationContext)
                        }

                        // Process and dispatch - processIncomingSms will await webhook completions
                        repository.processIncomingSms(
                            sender = sender,
                            body = fullBody,
                            receivedAt = timestamp,
                            simSlot = simSlot
                        )
                    } catch (_: Exception) {
                        // Guard against uncaught exceptions
                    } finally {
                        try {
                            if (wakeLock?.isHeld == true) {
                                wakeLock.release()
                            }
                        } catch (_: Exception) {}
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
