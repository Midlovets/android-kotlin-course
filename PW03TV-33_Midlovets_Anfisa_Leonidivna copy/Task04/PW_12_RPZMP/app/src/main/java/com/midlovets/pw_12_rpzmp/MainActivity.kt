package com.midlovets.pw_12_rpzmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.midlovets.pw_12_rpzmp.app.AuditErrorViewModel
import com.midlovets.pw_12_rpzmp.app.AuditErrorViewModelFactory
import com.midlovets.pw_12_rpzmp.app.AuditScreen
import com.midlovets.pw_12_rpzmp.data.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = AuditErrorViewModelFactory(
            api = RetrofitClient.api,
            context = applicationContext
        )

        setContent {
            val viewModel: AuditErrorViewModel = viewModel(factory = factory)
            AuditScreen(viewModel = viewModel)
        }
    }
}