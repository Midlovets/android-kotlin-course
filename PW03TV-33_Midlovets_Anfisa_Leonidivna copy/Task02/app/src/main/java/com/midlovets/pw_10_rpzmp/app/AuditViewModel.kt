package com.midlovets.pw_10_rpzmp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midlovets.pw_10_rpzmp.data.AuditApiService
import com.midlovets.pw_10_rpzmp.data.AuditStats
import com.midlovets.pw_10_rpzmp.data.BuildingAudit
import com.midlovets.pw_10_rpzmp.data.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuditViewModel : ViewModel() {
    private val api: AuditApiService = RetrofitClient.api

    // --- Кешування в пам'яті ---
    private var auditsCache = listOf<BuildingAudit>()
    private var statsCache: AuditStats? = null

    // --- StateFlows для реактивного оновлення UI ---
    private val _audits = MutableStateFlow<List<BuildingAudit>>(emptyList())
    val audits: StateFlow<List<BuildingAudit>> = _audits.asStateFlow()

    private val _stats = MutableStateFlow<AuditStats?>(null)
    val stats: StateFlow<AuditStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    private suspend fun <T> retryRequest(times: Int = 3, block: suspend () -> T): T {
        var currentAttempt = 0
        var lastError: Exception? = null
        while (currentAttempt < times) {
            try {
                return block()
            } catch (e: Exception) {
                lastError = e
                currentAttempt++
                delay(1000L * currentAttempt) // Експоненційна затримка
            }
        }
        throw lastError ?: Exception("Unknown error")
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val fetchedAudits = retryRequest { api.getAudits() }
                val fetchedStats = retryRequest { api.getAuditStats() }

                auditsCache = fetchedAudits
                statsCache = fetchedStats

                _audits.value = auditsCache
                _stats.value = statsCache
            } catch (e: Exception) {
                _error.value = "Помилка завантаження: ${e.localizedMessage}"

                if (auditsCache.isNotEmpty()) {
                    _audits.value = auditsCache
                    _stats.value = statsCache
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByEnergyClass(energyClass: String) {
        if (energyClass.isBlank()) {
            _audits.value = auditsCache
        } else {
            _audits.value = auditsCache.filter { it.energyClass.equals(energyClass, ignoreCase = true) }
        }
    }
}