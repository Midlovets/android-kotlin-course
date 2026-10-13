package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.BuildingAuditScreen
import com.example.myapplication.ui.theme.EnergyAuditTheme

/**
 * Точка входу: підключає тему та єдиний екран.
 * Моделі – у model, бізнес-логіка – у logic, UI – у ui (components, screens, theme).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnergyAuditTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BuildingAuditScreen()
                }
            }
        }
    }
}
