package com.midlovets.pw_09_rpzmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.midlovets.pw_09_rpzmp.app.AuditViewModel
import com.midlovets.pw_09_rpzmp.app.AuditViewModelFactory
import com.midlovets.pw_09_rpzmp.app.EnergyAuditScreen
import com.midlovets.pw_09_rpzmp.data.AuditDatabase
import com.midlovets.pw_09_rpzmp.data.AuditRepository
import com.midlovets.pw_09_rpzmp.data.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AuditDatabase.getDatabase(this)
        val repository = AuditRepository(database.auditDao(), RetrofitClient.api)
        val factory = AuditViewModelFactory(repository)

        setContent {
            val viewModel: AuditViewModel = viewModel(factory = factory)
            EnergyAuditScreen(viewModel = viewModel)
        }
    }
}
