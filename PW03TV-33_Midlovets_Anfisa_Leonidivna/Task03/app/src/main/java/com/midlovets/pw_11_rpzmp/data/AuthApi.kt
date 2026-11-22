package com.midlovets.pw_11_rpzmp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val role: String)
data class AuditReport(val id: Int, val address: String, val energyClass: String)

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("audits/protected")
    suspend fun getProtectedAudits(): Response<List<AuditReport>>

    @GET("audits/admin")
    suspend fun getAdminReports(): Response<List<AuditReport>>
}