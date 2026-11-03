package com.example.data.repository

import android.content.Context
import com.example.data.local.WisfyDatabase
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.ProcessedMessageFingerprint
import com.example.data.local.entities.ProcessedTransactionEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.domain.model.WisfyPayload
import com.example.domain.parser.ParserRegistry
import com.example.domain.privacy.PrivacyFilter
import com.example.network.DispatchResult
import com.example.network.WebhookDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

class WisfyRepository(private val context: Context) {
    private val db = WisfyDatabase.getDatabase(context)
    private val endpointDao = db.endpointDao()
    private val smsEventDao = db.smsEventDao()
    private val deliveryLogDao = db.deliveryLogDao()
    private val appSettingsDao = db.appSettingsDao()
    private val deduplicationDao = db.deduplicationDao()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Flow accessors
    val allEndpointsFlow: Flow<List<WebhookEndpointEntity>> = endpointDao.getAllEndpointsFlow()
    val allEventsFlow: Flow<List<SmsEventEntity>> = smsEventDao.getAllEventsFlow()
    val allLogsFlow: Flow<List<DeliveryLogEntity>> = deliveryLogDao.getAllLogsFlow()
    val settingsFlow: Flow<AppSettingsEntity?> = appSettingsDao.getSettingsFlow()

    val deliveredCountFlow: Flow<Int> = deliveryLogDao.getDeliveredCountFlow()
    val queuedCountFlow: Flow<Int> = deliveryLogDao.getQueuedCountFlow()
    val failedCountFlow: Flow<Int> = deliveryLogDao.getFailedCountFlow()
    val totalLogsCountFlow: Flow<Int> = deliveryLogDao.getTotalLogsCountFlow()

    suspend fun getSettings(): AppSettingsEntity {
        var settings = appSettingsDao.getSettings()
        if (settings == null) {
            val randomDeviceId = "device_" + UUID.randomUUID().toString().take(12)
            settings = AppSettingsEntity(deviceId = randomDeviceId)
            appSettingsDao.saveSettings(settings)
        }
        return settings
    }

    suspend fun saveSettings(settings: AppSettingsEntity) {
        appSettingsDao.saveSettings(settings)
    }

    suspend fun setServiceActive(active: Boolean) {
        appSettingsDao.setServiceActive(active)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        appSettingsDao.setOnboardingCompleted(completed)
    }

    suspend fun setMaskSmsBody(mask: Boolean) {
        appSettingsDao.setMaskSmsBody(mask)
    }

    suspend fun setForwardRawBody(forward: Boolean) {
        appSettingsDao.setForwardRawBody(forward)
    }

    suspend fun setAutoFilterOtp(filter: Boolean) {
        appSettingsDao.setAutoFilterOtp(filter)
    }

    suspend fun setRetryAndTimeout(retries: Int, timeoutSec: Int) {
        appSettingsDao.setRetryAndTimeout(retries, timeoutSec)
    }

    suspend fun setThemeMode(mode: String) {
        appSettingsDao.setThemeMode(mode)
    }

    // Endpoint CRUD
    suspend fun saveEndpoint(endpoint: WebhookEndpointEntity): Long {
        return endpointDao.insertEndpoint(endpoint)
    }

    suspend fun updateEndpoint(endpoint: WebhookEndpointEntity) {
        endpointDao.updateEndpoint(endpoint)
    }

    suspend fun deleteEndpoint(endpoint: WebhookEndpointEntity) {
        endpointDao.deleteEndpoint(endpoint)
    }

    suspend fun deleteEndpointById(id: Long) {
        endpointDao.deleteEndpointById(id)
    }

    // Process Incoming SMS Event with Persistent Two-Layer Deduplication (PRD Section 18)
    suspend fun processIncomingSms(
        sender: String,
        body: String,
        receivedAt: Long = System.currentTimeMillis(),
        simSlot: Int = 0
    ): ProcessResult {
        val settings = getSettings()

        if (!settings.backgroundServiceActive) {
            return ProcessResult.ServiceInactive
        }

        // ==========================================
        // Layer A — Incoming-message deduplication
        // ==========================================
        val timeBucket = receivedAt / 300_000 // 5-minute bucket
        val normalizedBody = body.trim().replace("\\s+".toRegex(), " ")
        val messageFingerprint = sha256("${sender.trim().lowercase()}:$normalizedBody:$timeBucket")

        val existingMessage = deduplicationDao.getMessageFingerprint(messageFingerprint)
        if (existingMessage != null) {
            // Duplicate incoming broadcast detected: suppress second forward event per PRD 18.2
            val duplicateEventId = generateEventId()
            val duplicateEntity = SmsEventEntity(
                eventId = duplicateEventId,
                eventType = "sms.duplicate_detected",
                sender = sender,
                rawBody = body,
                sanitizedBody = "[DUPLICATE BROADCAST SUPPRESSED]",
                receivedAt = receivedAt,
                deviceId = settings.deviceId,
                simSlot = simSlot,
                status = "duplicate",
                fingerprint = messageFingerprint,
                isMessageDuplicate = true,
                reviewReason = "Layer A Duplicate: Identical broadcast delivered within deduplication window"
            )
            smsEventDao.insertEvent(duplicateEntity)
            return ProcessResult.MessageDuplicateSuppressed(existingMessage.eventId)
        }

        // Record message fingerprint
        val newFingerprint = ProcessedMessageFingerprint(
            fingerprint = messageFingerprint,
            eventId = "",
            sender = sender,
            receivedAt = receivedAt
        )

        // Privacy check (OTP / Auth code)
        val isOtp = PrivacyFilter.isOtpOrAuthCode(sender, body)
        if (settings.autoFilterOtp && isOtp) {
            val eventId = generateEventId()
            deduplicationDao.insertMessageFingerprint(newFingerprint.copy(eventId = eventId))

            val sanitized = "[REDACTED OTP / AUTH CODE]"
            val eventEntity = SmsEventEntity(
                eventId = eventId,
                eventType = "sms.received",
                sender = sender,
                rawBody = if (settings.maskSmsBodyInLogs) sanitized else body,
                sanitizedBody = sanitized,
                receivedAt = receivedAt,
                deviceId = settings.deviceId,
                simSlot = simSlot,
                provider = "security_auth",
                status = "filtered_privacy",
                fingerprint = messageFingerprint,
                reviewReason = "Auto-filtered OTP / 2FA passcode"
            )
            smsEventDao.insertEvent(eventEntity)
            return ProcessResult.FilteredPrivacy(eventId)
        }

        // Parse content using provider profiles
        val parsed = ParserRegistry.parseMessage(sender, body)
        val eventId = generateEventId()
        deduplicationDao.insertMessageFingerprint(newFingerprint.copy(eventId = eventId))

        // Check if message is an outgoing cash-out / reversal
        if (parsed.isOutgoingOrReversal) {
            val eventEntity = SmsEventEntity(
                eventId = eventId,
                eventType = "sms.received",
                sender = sender,
                rawBody = body,
                sanitizedBody = if (settings.maskSmsBodyInLogs) PrivacyFilter.maskBody(body) else body,
                receivedAt = receivedAt,
                deviceId = settings.deviceId,
                simSlot = simSlot,
                provider = parsed.provider,
                status = "outgoing_filtered",
                fingerprint = messageFingerprint,
                reviewReason = parsed.reviewReason ?: "Outgoing transaction or debit alert"
            )
            smsEventDao.insertEvent(eventEntity)
            return ProcessResult.OutgoingFiltered(eventId)
        }

        // ==========================================
        // Layer B — Payment-transaction deduplication
        // ==========================================
        var isTransactionDuplicate = false
        val transactionId = parsed.transactionId
        val idempotencyKey = if (!transactionId.isNullOrBlank()) {
            "${parsed.provider.lowercase()}:${transactionId.trim()}"
        } else {
            "${parsed.provider.lowercase()}:$messageFingerprint"
        }

        if (!transactionId.isNullOrBlank()) {
            val existingTx = deduplicationDao.getProcessedTransaction(idempotencyKey)
            if (existingTx != null) {
                // Duplicate transaction reference detected: MUST NOT trigger a second payment webhook
                isTransactionDuplicate = true
                val duplicateEntity = SmsEventEntity(
                    eventId = eventId,
                    eventType = "sms.duplicate_detected",
                    sender = sender,
                    rawBody = body,
                    sanitizedBody = if (settings.maskSmsBodyInLogs) PrivacyFilter.maskBody(body) else body,
                    receivedAt = receivedAt,
                    deviceId = settings.deviceId,
                    simSlot = simSlot,
                    provider = parsed.provider,
                    transactionId = transactionId,
                    amount = parsed.amount,
                    currency = parsed.currency,
                    status = "duplicate",
                    fingerprint = messageFingerprint,
                    idempotencyKey = idempotencyKey,
                    isTransactionDuplicate = true,
                    reviewReason = "Layer B Duplicate: Transaction ID '$transactionId' was already processed for ${parsed.provider} in event ${existingTx.originalEventId}"
                )
                smsEventDao.insertEvent(duplicateEntity)
                return ProcessResult.TransactionDuplicateSuppressed(existingTx.originalEventId, idempotencyKey)
            } else {
                // Record processed transaction in database
                deduplicationDao.insertProcessedTransaction(
                    ProcessedTransactionEntity(
                        transactionKey = idempotencyKey,
                        provider = parsed.provider,
                        transactionId = transactionId,
                        amount = parsed.amount,
                        currency = parsed.currency,
                        originalEventId = eventId
                    )
                )
            }
        }

        val sanitizedBody = if (settings.maskSmsBodyInLogs) {
            PrivacyFilter.maskBody(body)
        } else {
            PrivacyFilter.redactSensitiveNumbers(body)
        }

        val eventEntity = SmsEventEntity(
            eventId = eventId,
            eventType = if (parsed.isPaymentCandidate) "sms.payment_candidate" else "sms.received",
            sender = sender,
            rawBody = if (settings.maskSmsBodyInLogs) sanitizedBody else body,
            sanitizedBody = sanitizedBody,
            receivedAt = receivedAt,
            schemaVersion = "1.1",
            deviceId = settings.deviceId,
            simSlot = simSlot,
            provider = parsed.provider,
            transactionId = parsed.transactionId,
            amount = parsed.amount,
            currency = parsed.currency,
            status = parsed.status,
            fingerprint = messageFingerprint,
            idempotencyKey = idempotencyKey,
            isMessageDuplicate = false,
            isTransactionDuplicate = false,
            senderMatch = parsed.senderMatch,
            templateMatch = parsed.templateMatch,
            serverVerified = false, // Never client verified per PRD Section 18.4
            reviewReason = parsed.reviewReason
        )
        smsEventDao.insertEvent(eventEntity)

        // Find matching active endpoints (considering SIM slot routing and sender filter)
        val activeEndpoints = endpointDao.getEnabledEndpoints()
        val matchingEndpoints = activeEndpoints.filter { ep ->
            val simMatches = (ep.simSlot == 0 || simSlot == 0 || ep.simSlot == simSlot)
            simMatches && matchesSenderFilter(ep.senderFilter, sender, body)
        }

        if (matchingEndpoints.isEmpty()) {
            return ProcessResult.NoMatchingEndpoints(eventId)
        }

        // Update persistent notification with active forwarding status
        com.example.service.WisfyForegroundService.updateStatus(
            context,
            "Wisfy SMS • Forwarding...",
            "From $sender -> Dispatched to ${matchingEndpoints.size} endpoint(s)"
        )

        // Dispatch to each matching endpoint concurrently and await completion before returning
        coroutineScope {
            matchingEndpoints.map { ep ->
                val log = DeliveryLogEntity(
                    eventId = eventId,
                    endpointId = ep.id,
                    endpointName = ep.name,
                    endpointUrl = ep.url,
                    status = "queued",
                    attemptCount = 1
                )
                val logId = deliveryLogDao.insertLog(log)

                val payload = WisfyPayload(
                    event = "sms.received",
                    eventType = eventEntity.eventType,
                    eventId = eventId,
                    schemaVersion = "1.1",
                    deviceId = settings.deviceId,
                    receivedAtMs = receivedAt,
                    sender = sender,
                    body = if (settings.forwardRawMessageBody) body else sanitizedBody,
                    provider = parsed.provider,
                    transactionId = parsed.transactionId,
                    amount = parsed.amount,
                    currency = parsed.currency,
                    status = parsed.status,
                    idempotencyKey = idempotencyKey,
                    isMessageDuplicate = false,
                    isTransactionDuplicate = false,
                    senderMatch = parsed.senderMatch,
                    templateMatch = parsed.templateMatch,
                    serverVerified = false,
                    simSlot = simSlot
                )

                async(Dispatchers.IO) {
                    executeDispatch(logId, ep, payload, maxRetries = settings.retryCount)
                }
            }.awaitAll()
        }

        // Restore notification back to idle listening state with last event summary
        com.example.service.WisfyForegroundService.updateStatus(
            context,
            "Wisfy SMS Gateway • Active",
            "Last event: $sender (${parsed.provider}) -> Dispatched"
        )

        return ProcessResult.Dispatched(eventId, matchingEndpoints.size)
    }

    private suspend fun executeDispatch(
        logId: Long,
        endpoint: WebhookEndpointEntity,
        payload: WisfyPayload,
        maxRetries: Int = 1
    ) {
        var attempt = 1
        var result: DispatchResult? = null
        val totalAttempts = maxOf(1, maxRetries)

        while (attempt <= totalAttempts) {
            deliveryLogDao.updateLogResult(
                id = logId,
                status = "sending",
                code = null,
                latency = 0,
                response = null,
                error = null,
                deliveredAt = null,
                attemptCount = attempt
            )

            result = WebhookDispatcher.dispatch(endpoint, payload)
            if (result.isSuccess) {
                break
            }

            if (attempt < totalAttempts) {
                // Exponential backoff: 1s, 2s, 4s...
                val backoffMs = (1000L * (1 shl (attempt - 1))).coerceAtMost(8000L)
                delay(backoffMs)
            }
            attempt++
        }

        val finalResult = result ?: DispatchResult(
            isSuccess = false,
            httpStatusCode = null,
            latencyMs = 0,
            responseSnippet = null,
            errorMessage = "No dispatch attempts were made"
        )
        val finalStatus = if (finalResult.isSuccess) "delivered" else "failed"

        deliveryLogDao.updateLogResult(
            id = logId,
            status = finalStatus,
            code = finalResult.httpStatusCode,
            latency = finalResult.latencyMs,
            response = finalResult.responseSnippet,
            error = finalResult.errorMessage,
            deliveredAt = if (finalResult.isSuccess) System.currentTimeMillis() else null,
            attemptCount = (attempt).coerceAtMost(totalAttempts)
        )
    }

    // Manual Retry (Preserves original eventId and idempotencyKey per PRD FR-09 & 18.2)
    suspend fun retryDelivery(logId: Long): DispatchResult? {
        val log = deliveryLogDao.getLogById(logId) ?: return null
        val endpoint = endpointDao.getEndpointById(log.endpointId) ?: return null
        val event = smsEventDao.getEventById(log.eventId) ?: return null
        val settings = getSettings()

        val payload = WisfyPayload(
            event = "sms.received",
            eventType = event.eventType,
            eventId = event.eventId, // Preserves same event ID
            schemaVersion = "1.1",
            deviceId = settings.deviceId,
            receivedAtMs = event.receivedAt,
            sender = event.sender,
            body = if (settings.forwardRawMessageBody) event.rawBody else event.sanitizedBody,
            provider = event.provider,
            transactionId = event.transactionId,
            amount = event.amount,
            currency = event.currency,
            status = event.status,
            idempotencyKey = event.idempotencyKey,
            isMessageDuplicate = event.isMessageDuplicate,
            isTransactionDuplicate = event.isTransactionDuplicate,
            senderMatch = event.senderMatch,
            templateMatch = event.templateMatch,
            serverVerified = false
        )

        deliveryLogDao.updateLogResult(
            id = logId,
            status = "sending",
            code = null,
            latency = 0,
            response = null,
            error = null,
            deliveredAt = null,
            attemptCount = log.attemptCount + 1
        )

        val result = WebhookDispatcher.dispatch(endpoint, payload)
        val finalStatus = if (result.isSuccess) "delivered" else "failed"

        deliveryLogDao.updateLogResult(
            id = logId,
            status = finalStatus,
            code = result.httpStatusCode,
            latency = result.latencyMs,
            response = result.responseSnippet,
            error = result.errorMessage,
            deliveredAt = if (result.isSuccess) System.currentTimeMillis() else null,
            attemptCount = log.attemptCount + 1
        )

        return result
    }

    // Resend / Re-dispatch full SMS event across all matching active endpoints (Requested: "peding or failed resend option")
    suspend fun resendSmsEvent(eventId: String): List<DispatchResult> {
        val event = smsEventDao.getEventById(eventId) ?: return emptyList()
        val endpoints: List<WebhookEndpointEntity> = endpointDao.getEnabledEndpoints()
        val settings = getSettings()
        val results = mutableListOf<DispatchResult>()

        val matchingEndpoints = endpoints.filter { ep ->
            val simMatches = (ep.simSlot == 0 || event.simSlot == 0 || ep.simSlot == event.simSlot)
            simMatches && matchesSenderFilter(ep.senderFilter, event.sender, event.sanitizedBody)
        }

        val payload = WisfyPayload(
            event = "sms.received",
            eventType = event.eventType,
            eventId = event.eventId,
            schemaVersion = "1.1",
            deviceId = settings.deviceId,
            receivedAtMs = event.receivedAt,
            sender = event.sender,
            body = if (settings.forwardRawMessageBody) event.rawBody else event.sanitizedBody,
            provider = event.provider,
            transactionId = event.transactionId,
            amount = event.amount,
            currency = event.currency,
            status = event.status,
            idempotencyKey = event.idempotencyKey,
            isMessageDuplicate = event.isMessageDuplicate,
            isTransactionDuplicate = event.isTransactionDuplicate,
            senderMatch = event.senderMatch,
            templateMatch = event.templateMatch,
            serverVerified = false,
            simSlot = event.simSlot
        )

        for (endpoint in matchingEndpoints) {
            val logId = deliveryLogDao.insertLog(
                DeliveryLogEntity(
                    eventId = event.eventId,
                    endpointId = endpoint.id,
                    endpointName = endpoint.name,
                    endpointUrl = endpoint.url,
                    status = "sending",
                    attemptCount = 1
                )
            )

            val result = WebhookDispatcher.dispatch(endpoint, payload)
            val finalStatus = if (result.isSuccess) "delivered" else "failed"

            deliveryLogDao.updateLogResult(
                id = logId,
                status = finalStatus,
                code = result.httpStatusCode,
                latency = result.latencyMs,
                response = result.responseSnippet,
                error = result.errorMessage,
                deliveredAt = if (result.isSuccess) System.currentTimeMillis() else null,
                attemptCount = 1
            )
            results.add(result)
        }
        return results
    }

    // Test Webhook
    suspend fun testEndpoint(endpoint: WebhookEndpointEntity): DispatchResult {
        val settings = getSettings()
        val testPayload = WisfyPayload.createSyntheticTest(settings.deviceId)

        val result = WebhookDispatcher.dispatch(endpoint, testPayload)

        val updatedEndpoint = endpoint.copy(
            lastTestedAt = System.currentTimeMillis(),
            lastTestSuccess = result.isSuccess,
            lastTestStatus = result.httpStatusCode,
            lastTestLatencyMs = result.latencyMs,
            lastTestMessage = if (result.isSuccess) "Accepted (${result.latencyMs}ms)" else (result.errorMessage ?: "Failed")
        )
        endpointDao.updateEndpoint(updatedEndpoint)

        return result
    }

    // Clear Logs & Queue & Deduplication
    suspend fun clearLogsAndQueue() {
        deliveryLogDao.deleteAllLogs()
        smsEventDao.deleteAllEvents()
        deduplicationDao.clearMessageFingerprints()
        deduplicationDao.clearProcessedTransactions()
    }

    // Export Redacted Diagnostic Report
    suspend fun exportDiagnosticReport(): String {
        val settings = getSettings()
        val endpoints = endpointDao.getAllEndpoints()
        val totalTxCount = deduplicationDao.getTotalProcessedTransactionsCount()

        val report = JSONObject()
        report.put("app", "Wisfy SMS")
        report.put("developer", "Shahria Nafiz Sajjad")
        report.put("schema_version", "1.1")
        report.put("timestamp", System.currentTimeMillis())
        report.put("device_id", settings.deviceId)
        report.put("deduplication_layer_a", "active")
        report.put("deduplication_layer_b", "active")
        report.put("total_processed_transactions", totalTxCount)

        val epArray = JSONArray()
        for (ep in endpoints) {
            val epObj = JSONObject()
            epObj.put("id", ep.id)
            epObj.put("name", ep.name)
            epObj.put("url", ep.url)
            epObj.put("auth_type", ep.authType)
            epObj.put("auth_token_configured", ep.authToken.isNotBlank())
            epObj.put("enabled", ep.enabled)
            epObj.put("last_tested_at", ep.lastTestedAt)
            epObj.put("last_test_success", ep.lastTestSuccess)
            epArray.put(epObj)
        }
        report.put("endpoints", epArray)
        return report.toString(2)
    }

    private fun matchesSenderFilter(filter: String, sender: String, body: String): Boolean {
        if (filter.isBlank()) return true
        val tokens = filter.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return true

        val senderLower = sender.lowercase()
        val bodyLower = body.lowercase()

        for (token in tokens) {
            if (senderLower.contains(token) || bodyLower.contains(token)) {
                return true
            }
        }
        return false
    }

    private fun generateEventId(): String {
        return "evt_" + System.currentTimeMillis().toString(36) + "_" + UUID.randomUUID().toString().take(6)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

sealed class ProcessResult {
    data class Dispatched(val eventId: String, val endpointCount: Int) : ProcessResult()
    data class NoMatchingEndpoints(val eventId: String) : ProcessResult()
    data class MessageDuplicateSuppressed(val originalEventId: String) : ProcessResult()
    data class TransactionDuplicateSuppressed(val originalEventId: String, val transactionKey: String) : ProcessResult()
    data class OutgoingFiltered(val eventId: String) : ProcessResult()
    data class FilteredPrivacy(val eventId: String) : ProcessResult()
    object ServiceInactive : ProcessResult()
}
