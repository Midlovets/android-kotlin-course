package com.midlovets.pw_12_rpzmp.data

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

// Модель даних
data class BuildingAudit(
    val id: Int,
    val address: String,
    val energyClass: String
)

interface AuditApiService {
    // Параметр simulateError дозволяє симулювати помилки для тестування
    @GET("audits/today")
    suspend fun getTodayAudits(@Query("simulateError") simulateError: Int? = null): List<BuildingAudit>
}

// Інтерсептор для підміни сервера та генерації помилок (mocking)
class MockErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        Thread.sleep(1000) // Імітація затримки мережі

        // Перевіряємо, чи є запит на симуляцію помилки
        val simulateErrorCode = request.url.queryParameter("simulateError")?.toIntOrNull()

        if (simulateErrorCode != null && simulateErrorCode != 200) {
            return Response.Builder()
                .code(simulateErrorCode)
                .message("Simulated Error $simulateErrorCode")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }

        val jsonResponse = """[
            {"id": 1, "address": "вул. Хрещатик, 1", "energyClass": "B"},
            {"id": 2, "address": "просп. Науки, 4", "energyClass": "A"}
        ]"""

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(jsonResponse.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}