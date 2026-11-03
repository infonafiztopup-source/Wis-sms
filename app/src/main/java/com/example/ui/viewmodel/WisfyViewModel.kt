package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WisfyApp
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.network.DispatchResult
import com.example.service.WisfyForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WisfyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as WisfyApp).repository

    val endpoints: StateFlow<List<WebhookEndpointEntity>> = repository.allEndpointsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<SmsEventEntity>> = repository.allEventsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<DeliveryLogEntity>> = repository.allLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettingsEntity?> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val deliveredCount: StateFlow<Int> = repository.deliveredCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val queuedCount: StateFlow<Int> = repository.queuedCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val failedCount: StateFlow<Int> = repository.failedCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalLogsCount: StateFlow<Int> = repository.totalLogsCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isTestingEndpoint = MutableStateFlow(false)
    val isTestingEndpoint: StateFlow<Boolean> = _isTestingEndpoint.asStateFlow()

    private val _lastTestResult = MutableStateFlow<DispatchResult?>(null)
    val lastTestResult: StateFlow<DispatchResult?> = _lastTestResult.asStateFlow()

    init {
        // Ensure initial settings exist and seed initial demo endpoint if completely empty
        viewModelScope.launch {
            val currentSettings = repository.getSettings()
            if (currentSettings.backgroundServiceActive) {
                WisfyForegroundService.start(application)
            }
        }
    }

    fun saveEndpoint(endpoint: WebhookEndpointEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveEndpoint(endpoint)
            onComplete()
        }
    }

    fun deleteEndpoint(id: Long) {
        viewModelScope.launch {
            repository.deleteEndpointById(id)
        }
    }

    fun toggleEndpoint(endpoint: WebhookEndpointEntity) {
        viewModelScope.launch {
            repository.updateEndpoint(endpoint.copy(enabled = !endpoint.enabled))
        }
    }

    fun testEndpoint(endpoint: WebhookEndpointEntity, onResult: (DispatchResult) -> Unit = {}) {
        viewModelScope.launch {
            _isTestingEndpoint.value = true
            val result = repository.testEndpoint(endpoint)
            _lastTestResult.value = result
            _isTestingEndpoint.value = false
            onResult(result)
        }
    }

    fun retryDelivery(logId: Long, onResult: (DispatchResult?) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.retryDelivery(logId)
            onResult(result)
        }
    }

    fun resendSmsEvent(eventId: String, onComplete: (List<DispatchResult>) -> Unit = {}) {
        viewModelScope.launch {
            val results = repository.resendSmsEvent(eventId)
            onComplete(results)
        }
    }

    fun simulateIncomingSms(sender: String, body: String, simSlot: Int = 1, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.processIncomingSms(sender = sender, body = body, simSlot = simSlot)
            onComplete()
        }
    }

    fun setServiceActive(active: Boolean) {
        viewModelScope.launch {
            repository.setServiceActive(active)
            if (active) {
                WisfyForegroundService.start(getApplication())
            } else {
                WisfyForegroundService.stop(getApplication())
            }
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            repository.setOnboardingCompleted(completed)
        }
    }

    fun setMaskSmsBody(mask: Boolean) {
        viewModelScope.launch {
            repository.setMaskSmsBody(mask)
        }
    }

    fun setForwardRawBody(forward: Boolean) {
        viewModelScope.launch {
            repository.setForwardRawBody(forward)
        }
    }

    fun setAutoFilterOtp(filter: Boolean) {
        viewModelScope.launch {
            repository.setAutoFilterOtp(filter)
        }
    }

    fun setRetryAndTimeout(retries: Int, timeoutSec: Int) {
        viewModelScope.launch {
            repository.setRetryAndTimeout(retries, timeoutSec)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun clearLogsAndQueue() {
        viewModelScope.launch {
            repository.clearLogsAndQueue()
        }
    }

    fun exportDiagnosticReport(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val report = repository.exportDiagnosticReport()
            onResult(report)
        }
    }
}
