package com.midlovets.pw_09_rpzmp.data

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody


class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val uri = request.url().uri().toString()
        val method = request.method()

        // Генеруємо фейковий JSON залежно від методу та URL
        val responseString = when {
            uri.contains("audits") && method == "GET" -> {
                """
                [
                    {"id": 1, "address": "вул. Хрещатик, 1", "consumption_kwh": 450.5, "energy_class": "C", "audit_date": "2026-10-15"},
                    {"id": 2, "address": "просп. Берестейський, 37", "consumption_kwh": 320.0, "energy_class": "A", "audit_date": "2026-11-01"},
                    {"id": 3, "address": "вул. Політехнічна, 14", "consumption_kwh": 510.2, "energy_class": "D", "audit_date": "2026-11-10"}
                ]
                """.trimIndent()
            }
            uri.contains("audits") && method == "POST" -> {
                val buffer = okio.Buffer()
                request.body()?.writeTo(buffer)
                val bodyStr = buffer.readUtf8()
                if (bodyStr.isNotBlank()) bodyStr else """{"id": 99, "address": "Нова будівля", "consumption_kwh": 100.0, "energy_class": "B", "audit_date": "2026-11-17"}"""
            }
            method == "DELETE" -> {
                "{}" // Порожній успішний JSON для видалення
            }
            else -> "[]"
        }

        // Формуємо HTTP-відповідь із фейковим JSON
        return Response.Builder()
            .code(200)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(ResponseBody.create(MediaType.parse("application/json"), responseString))
            .addHeader("content-type", "application/json")
            .build()
    }
}