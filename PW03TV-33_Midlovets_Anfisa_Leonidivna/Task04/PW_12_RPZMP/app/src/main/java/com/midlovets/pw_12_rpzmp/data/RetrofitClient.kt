package com.midlovets.pw_12_rpzmp.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost/"

    val api: AuditApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(MockErrorInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuditApiService::class.java)
    }
}