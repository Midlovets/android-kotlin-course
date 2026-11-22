package com.midlovets.pw_11_rpzmp.app

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthEnergyAuditApp(viewModel: AuthViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val error by viewModel.error.collectAsState()
    val audits by viewModel.audits.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {

        error?.let {
            Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
        }

        if (!isLoggedIn) {
            Text("Вхід до системи (Енергоаудит)", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логін (admin / auditor)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Увійти")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ви увійшли як: $role", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { viewModel.logout() }) {
                    Text("Вийти")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.loadProtectedAudits() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Завантажити звичайні аудити (USER)")
            }

            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = { viewModel.loadAdminReports() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Секретні звіти (Тільки для ADMIN)")
            }


            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(audits) { audit ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Адреса: ${audit.address}", style = MaterialTheme.typography.titleMedium)
                            Text("Клас енергоефективності: ${audit.energyClass}")
                        }
                    }
                }
            }
        }
    }
}