package com.midlovets.pw_11_rpzmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.midlovets.pw_11_rpzmp.app.AuthEnergyAuditApp
import com.midlovets.pw_11_rpzmp.app.AuthViewModel
import com.midlovets.pw_11_rpzmp.app.AuthViewModelFactory
import com.midlovets.pw_11_rpzmp.data.AuthStorage
import com.midlovets.pw_11_rpzmp.data.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authStorage = AuthStorage(this)
        val authApi = RetrofitClient.createAuthApi(authStorage)
        val factory = AuthViewModelFactory(authApi, authStorage)

        setContent {
            val viewModel: AuthViewModel = viewModel(factory = factory)
            AuthEnergyAuditApp(viewModel = viewModel)
        }
    }
}
