package com.midlovets.pw_10_rpzmp.data

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AuditApiService {
    @GET("audits")
    suspend fun getAudits(): List<BuildingAudit>

    @GET("audits/stats")
    suspend fun getAuditStats(): AuditStats
}

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url().uri().toString()

        val responseString = when {
            uri.contains("audits/stats") -> """
                {
                    "total_buildings": 3,
                    "avg_consumption": 285.5,
                    "top_energy_class": "B"
                }
            """.trimIndent()

            uri.contains("audits") -> """
                [
                    {
                        "id": 1, 
                        "address": "вул. Хрещатик, 12", 
                        "energy_class": "C", 
                        "consumption_kwh": 350.5,
                        "recommendations": ["Утеплити фасад", "Замінити вікна"]
                    },
                    {
                        "id": 2, 
                        "address": "просп. Перемоги, 37", 
                        "energy_class": "A", 
                        "consumption_kwh": 120.0,
                        "recommendations": ["Оптимізувати освітлення"]
                    },
                    {
                        "id": 3, 
                        "address": "вул. Політехнічна, 14", 
                        "energy_class": "D", 
                        "consumption_kwh": 386.0,
                        "recommendations": ["Встановити тепловий насос", "Модернізувати дах"]
                    }
                ]
            """.trimIndent()

            else -> "[]"
        }

        Thread.sleep(1000)

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(ResponseBody.create(MediaType.parse("application/json"), responseString))
            .addHeader("content-type", "application/json")
            .build()
    }
}

object RetrofitClient {
    private const val BASE_URL = "http://localhost/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MockInterceptor())
        .build()

    val api: AuditApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuditApiService::class.java)
    }
}