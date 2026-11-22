package com.midlovets.pw_11_rpzmp.data

import android.content.Context
import android.content.SharedPreferences

class AuthStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokenAndRole(token: String, role: String) {
        prefs.edit().putString("TOKEN", token).putString("ROLE", role).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun getToken(): String? = prefs.getString("TOKEN", null)
    fun getRole(): String? = prefs.getString("ROLE", null)
}