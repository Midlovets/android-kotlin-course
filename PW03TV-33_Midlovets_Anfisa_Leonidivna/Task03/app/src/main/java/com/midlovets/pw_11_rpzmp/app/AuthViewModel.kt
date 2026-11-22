package com.midlovets.pw_11_rpzmp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.midlovets.pw_11_rpzmp.data.AuditReport
import com.midlovets.pw_11_rpzmp.data.AuthApi
import com.midlovets.pw_11_rpzmp.data.AuthStorage
import com.midlovets.pw_11_rpzmp.data.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class AuthViewModel(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(authStorage.getToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow(authStorage.getRole())
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _audits = MutableStateFlow<List<AuditReport>>(emptyList())
    val audits: StateFlow<List<AuditReport>> = _audits.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun login(user: String, pass: String) {
        if (user.isBlank() || pass.isBlank()) {
            _error.value = "Логін і пароль не можуть бути порожніми"
            return
        }

        viewModelScope.launch {
            try {
                val response = authApi.login(LoginRequest(user, pass))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    authStorage.saveTokenAndRole(data.token, data.role)
                    _userRole.value = data.role
                    _isLoggedIn.value = true
                    _error.value = null
                } else {
                    _error.value = "Помилка авторизації: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Помилка мережі: ${e.localizedMessage}"
            }
        }
    }

    fun logout() {
        authStorage.clear()
        _isLoggedIn.value = false
        _userRole.value = null
        _audits.value = emptyList()
    }

    fun loadProtectedAudits() {
        viewModelScope.launch {
            try {
                val response = authApi.getProtectedAudits()
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun loadAdminReports() {
        viewModelScope.launch {
            try {
                val response = authApi.getAdminReports()
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    private fun handleResponse(response: retrofit2.Response<List<AuditReport>>) {
        if (response.isSuccessful) {
            _audits.value = response.body() ?: emptyList()
            _error.value = null
        } else {
            when (response.code()) {
                401 -> {
                    _error.value = "Сесія закінчилась. Будь ласка, увійдіть знову."
                    logout()
                }
                403 -> _error.value = "Відмовлено в доступі! Потрібні права адміністратора."
                else -> _error.value = "Помилка: ${response.code()}"
            }
        }
    }
}

class AuthViewModelFactory(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(authApi, authStorage) as T
    }
}