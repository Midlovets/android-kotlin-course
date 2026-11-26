package com.midlovets.pw_09_rpzmp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.midlovets.pw_09_rpzmp.data.AuditRepository
import com.midlovets.pw_09_rpzmp.data.BuildingAudit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EnergyAuditState(
    val audits: List<BuildingAudit> = emptyList(),
    val isLoading: Boolean = false,
    val address: String = "",
    val consumption: String = "",
    val energyClass: String = "",
    val searchQuery: String = "",
    val sortDescending: Boolean = false
)

class AuditViewModel(private val repository: AuditRepository) : ViewModel() {

    private val _state = MutableStateFlow(EnergyAuditState())
    val state: StateFlow<EnergyAuditState> = _state.asStateFlow()

    private var currentAudits = listOf<BuildingAudit>()

    fun updateAddress(value: String) { _state.update { it.copy(address = value) } }
    fun updateConsumption(value: String) { _state.update { it.copy(consumption = value) } }
    fun updateEnergyClass(value: String) { _state.update { it.copy(energyClass = value) } }
    
    fun updateSearchQuery(value: String) {
        _state.update { it.copy(searchQuery = value) }
        updateAuditsList()
    }
    
    fun toggleSort() {
        _state.update { it.copy(sortDescending = !it.sortDescending) }
        updateAuditsList()
    }

    init {
        loadAudits()
    }

    private fun updateAuditsList() {
        val currentState = _state.value
        var filtered = if (currentState.searchQuery.isBlank()) {
            currentAudits
        } else {
            currentAudits.filter { it.address.contains(currentState.searchQuery, ignoreCase = true) }
        }

        filtered = if (currentState.sortDescending) {
            filtered.sortedByDescending { it.consumptionKwh }
        } else {
            filtered.sortedBy { it.consumptionKwh }
        }

        _state.update { it.copy(audits = filtered) }
    }

    fun loadAudits(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAudits(forceRefresh).onSuccess { data ->
                currentAudits = data
                updateAuditsList()
            }.onFailure {
                // Обробка помилок для UI
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun addAudit() {
        val currentState = _state.value
        val kwh = currentState.consumption.toDoubleOrNull()
        if (currentState.address.isNotBlank() && kwh != null && currentState.energyClass.isNotBlank()) {
            viewModelScope.launch {
                val newId = (currentAudits.maxOfOrNull { it.id } ?: 0) + 1
                val newAudit = BuildingAudit(newId, currentState.address, kwh, currentState.energyClass.uppercase(), "2026-11-17")
                repository.addAudit(newAudit)
                
                _state.update { it.copy(
                    address = "",
                    consumption = "",
                    energyClass = ""
                ) }
                
                loadAudits()
            }
        }
    }

    fun deleteAudit(id: Int) {
        viewModelScope.launch {
            repository.deleteAudit(id)
            loadAudits()
        }
    }
}

class AuditViewModelFactory(private val repository: AuditRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}