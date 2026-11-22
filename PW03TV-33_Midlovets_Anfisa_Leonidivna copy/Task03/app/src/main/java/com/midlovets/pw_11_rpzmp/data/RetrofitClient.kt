package com.midlovets.pw_11_rpzmp.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun createAuthApi(authStorage: AuthStorage): AuthApi {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authStorage))
            .addInterceptor(MockAuthBackendInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}