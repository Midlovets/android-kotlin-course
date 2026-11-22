package com.midlovets.pw_12_rpzmp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

object ErrorHandler {

    private fun logErrorToFile(context: Context, message: String) {
        try {
            val file = File(context.filesDir, "audit_error_log.txt")
            FileOutputStream(file, true).bufferedWriter().use { writer ->
                writer.write("${System.currentTimeMillis()}: $message\n")
            }
        } catch (e: Exception) {
            Log.e("ErrorHandler", "Не вдалося записати лог: ${e.localizedMessage}")
        }
    }

    fun handleException(context: Context, e: Throwable): String {
        val errorMessage = when (e) {
            is HttpException -> {
                when (e.code()) {
                    400 -> "Некоректний запит. Перевірте дані (400)."
                    401 -> "Помилка авторизації. Увійдіть у систему знову (401)."
                    403 -> "Доступ заборонено (403)."
                    404 -> "Ресурс або звіт не знайдено (404)."
                    500, 502, 503, 504 -> "Внутрішня помилка сервера. Спробуйте пізніше (${e.code()})."
                    else -> "Помилка сервера: ${e.code()}"
                }
            }
            is IOException -> "Помилка мережі. Перевірте з'єднання."
            else -> "Невідома помилка: ${e.localizedMessage}"
        }

        Log.e("AuditErrorHandler", errorMessage, e)
        logErrorToFile(context, errorMessage)

        return errorMessage
    }
}