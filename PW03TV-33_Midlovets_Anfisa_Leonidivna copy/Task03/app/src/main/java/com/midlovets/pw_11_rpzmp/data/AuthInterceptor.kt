package com.midlovets.pw_11_rpzmp.data

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class AuthInterceptor(private val authStorage: AuthStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = authStorage.getToken()

        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(newRequest)
    }
}

class MockAuthBackendInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val uri = request.url.encodedPath

        var code = 200
        var jsonResponse = ""

        when (uri) {
            "/login" -> {
                val bodyString = request.body?.let {
                    val buffer = okio.Buffer()
                    it.writeTo(buffer)
                    buffer.readUtf8()
                } ?: ""

                val json = JSONObject(bodyString)
                val user = json.optString("username")
                val pass = json.optString("password")

                when (user) {
                    "admin" if pass == "admin" -> {
                        jsonResponse = """{"token": "mock-jwt-admin-token-123", "role": "ADMIN"}"""
                    }
                    "auditor" if pass == "auditor" -> {
                        jsonResponse = """{"token": "mock-jwt-user-token-456", "role": "USER"}"""
                    }
                    else -> {
                        code = 401
                        jsonResponse = """{"error": "Невірний логін або пароль"}"""
                    }
                }
            }
            "/audits/protected", "/audits/admin" -> {
                val authHeader = request.header("Authorization")
                if (authHeader == null || !authHeader.startsWith("Bearer mock-jwt-")) {
                    code = 401
                    jsonResponse = """{"error": "Неавторизований доступ. Увійдіть у систему."}"""
                } else {
                    val role = if (authHeader.contains("admin")) "ADMIN" else "USER"

                    if (uri == "/audits/admin" && role != "ADMIN") {
                        code = 403
                        jsonResponse = """{"error": "Доступ заборонено. Потрібні права адміністратора."}"""
                    }
                    else {
                        jsonResponse = if (role == "ADMIN") """[
                            {"id": 1, "address": "Хрещатик, 1", "energyClass": "B"},
                            {"id": 2, "address": "Політехнічна, 14", "energyClass": "D"},
                            {"id": 3, "address": "Кавказька, 9", "energyClass": "F"},
                            {"id": 4, "address": "Уманська, 26", "energyClass": "C"}
                        ]""" else """[
                            {"id": 1, "address": "Хрещатик, 1", "energyClass": "B"},
                            {"id": 2, "address": "Політехнічна, 14", "energyClass": "D"}
                        ]"""
                    }
                }
            }
            else -> {
                code = 404
                jsonResponse = "{}"
            }
        }

        Thread.sleep(800)

        return Response.Builder()
            .code(code)
            .message(if (code == 200) "OK" else "Error")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(jsonResponse.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}