package com.midlovets.pw_12_rpzmp.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.midlovets.pw_12_rpzmp.data.AuditApiService
import com.midlovets.pw_12_rpzmp.data.ErrorHandler
import com.midlovets.pw_12_rpzmp.data.isInternetAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Функція перевірки наявності підключення до Інтернету
class AuditErrorViewModel(
    private val api: AuditApiService,
    private val context: Context // Передаємо контекст для ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditUiState())
    val uiState: StateFlow<AuditUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun fetchAuditsWithRetry(errorCodeToSimulate: Int? = null) {
        if (!isInternetAvailable(context)) {
            _errorMessage.value = "Немає підключення до Інтернету"
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var attempt = 0
            val maxRetries = 3
            val retryDelayMs = 2000L

            while (attempt < maxRetries) {
                try {
                    val response = api.getTodayAudits(simulateError = errorCodeToSimulate)

                    _uiState.value = _uiState.value.copy(audits = response, isLoading = false)
                    return@launch

                } catch (e: Throwable) {
                    attempt++
                    val formattedMessage = ErrorHandler.handleException(context, e)

                    val isRetryableError = e is IOException || (e is HttpException && e.code() >= 500)

                    if (isRetryableError && attempt < maxRetries) {
                        _errorMessage.value = "$formattedMessage (Спроба $attempt з $maxRetries...)"
                        delay(retryDelayMs)
                        continue
                    } else {
                        _errorMessage.value = formattedMessage
                        break
                    }
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}

class AuditErrorViewModelFactory(
    private val api: AuditApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuditErrorViewModel(api, context) as T
    }
}