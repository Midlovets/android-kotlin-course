package com.midlovets.pw_10_rpzmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.midlovets.pw_10_rpzmp.app.EnergyAuditScreen
import com.midlovets.pw_10_rpzmp.ui.theme.PW_10_RPZMPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PW_10_RPZMPTheme {
                EnergyAuditScreen()
            }
        }
    }
}
