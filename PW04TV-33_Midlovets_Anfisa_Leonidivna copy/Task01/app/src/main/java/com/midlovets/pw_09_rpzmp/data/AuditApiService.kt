package com.midlovets.pw_09_rpzmp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuditApiService {
    @GET("audits")
    suspend fun getAudits(): List<BuildingAudit>

    @POST("audits")
    suspend fun addAudit(@Body audit: BuildingAudit): Response<BuildingAudit>

    @DELETE("audits/{id}")
    suspend fun deleteAudit(@Path("id") id: Int): Response<Unit>
}