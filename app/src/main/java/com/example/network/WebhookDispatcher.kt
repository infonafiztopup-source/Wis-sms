package com.example.network

import com.example.data.local.entities.WebhookEndpointEntity
import com.example.domain.model.WisfyPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.util.concurrent.TimeUnit

data class DispatchResult(
    val isSuccess: Boolean,
    val httpStatusCode: Int?,
    val latencyMs: Long,
    val responseSnippet: String?,
    val errorMessage: String?
)

object WebhookDispatcher {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun dispatch(
        endpoint: WebhookEndpointEntity,
        payload: WisfyPayload
    ): DispatchResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        // URL validation
        val uri = try {
            URI(endpoint.url.trim())
        } catch (e: Exception) {
            return@withContext DispatchResult(
                isSuccess = false,
                httpStatusCode = null,
                latencyMs = 0,
                responseSnippet = null,
                errorMessage = "Malformed URL format: ${e.message}"
            )
        }

        if (uri.scheme == null || (!uri.scheme.equals("https", ignoreCase = true) && !uri.scheme.equals("http", ignoreCase = true))) {
            return@withContext DispatchResult(
                isSuccess = false,
                httpStatusCode = null,
                latencyMs = 0,
                responseSnippet = null,
                errorMessage = "Unsupported URL scheme: Only HTTPS (or development HTTP) is permitted."
            )
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(endpoint.timeoutSec.toLong(), TimeUnit.SECONDS)
            .readTimeout(endpoint.timeoutSec.toLong(), TimeUnit.SECONDS)
            .writeTimeout(endpoint.timeoutSec.toLong(), TimeUnit.SECONDS)
            .followRedirects(false) // PRD: Do not follow redirects to unvalidated destinations automatically
            .build()

        val jsonString = payload.toJsonString()
        val requestBody = jsonString.toRequestBody(jsonMediaType)

        val requestBuilder = Request.Builder()
            .url(endpoint.url.trim())
            .post(requestBody)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .header("User-Agent", "Wisfy-Gateway/1.2 (Android; PHP-Node-HTML-Compatible)")
            .header("X-Wisfy-Event", payload.event)
            .header("X-Wisfy-Event-Id", payload.eventId)
            .header("X-Wisfy-Schema-Version", payload.schemaVersion)
            .header("X-Wisfy-Sim-Slot", payload.simSlot.toString())

        // Auth Header with smart Bearer prefix handling
        if (endpoint.authType.equals("Bearer", ignoreCase = true) && endpoint.authToken.isNotBlank()) {
            val rawToken = endpoint.authToken.trim()
            val finalToken = if (rawToken.startsWith("Bearer ", ignoreCase = true)) rawToken else "Bearer $rawToken"
            requestBuilder.header("Authorization", finalToken)
        } else if (endpoint.authType.equals("Custom", ignoreCase = true) && endpoint.authToken.isNotBlank()) {
            requestBuilder.header("X-Api-Key", endpoint.authToken.trim())
        }


        // Custom headers
        try {
            if (endpoint.customHeadersJson.isNotBlank() && endpoint.customHeadersJson != "{}") {
                val headersObj = JSONObject(endpoint.customHeadersJson)
                val keys = headersObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    requestBuilder.header(key, headersObj.getString(key))
                }
            }
        } catch (_: Exception) {
            // Ignore malformed custom headers
        }

        val request = requestBuilder.build()

        try {
            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                val code = response.code
                val isSuccess = response.isSuccessful // 2xx HTTP response per PRD FR-07

                val responseBody = response.body?.string() ?: ""
                val snippet = if (responseBody.length > 300) responseBody.take(300) + "..." else responseBody

                DispatchResult(
                    isSuccess = isSuccess,
                    httpStatusCode = code,
                    latencyMs = latency,
                    responseSnippet = snippet.ifBlank { "HTTP $code ${response.message}" },
                    errorMessage = if (!isSuccess) "HTTP $code: ${response.message}" else null
                )
            }
        } catch (e: IOException) {
            val latency = System.currentTimeMillis() - startTime
            DispatchResult(
                isSuccess = false,
                httpStatusCode = null,
                latencyMs = latency,
                responseSnippet = null,
                errorMessage = e.localizedMessage ?: "Network or connection timeout"
            )
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            DispatchResult(
                isSuccess = false,
                httpStatusCode = null,
                latencyMs = latency,
                responseSnippet = null,
                errorMessage = "Execution error: ${e.message}"
            )
        }
    }
}
