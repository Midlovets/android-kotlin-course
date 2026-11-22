package com.midlovets.pw_09_rpzmp.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnergyAuditScreen(viewModel: AuditViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Text("Енергоаудит будівель", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                label = { Text("Пошук за адресою") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = viewModel::toggleSort) {
                Text(if (state.sortDescending) "Сортувати ↓" else "Сортувати ↑")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::updateAddress,
                label = { Text("Адреса") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = state.consumption,
                onValueChange = viewModel::updateConsumption,
                label = { Text("кВт·год") },
                modifier = Modifier.width(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.energyClass,
                onValueChange = viewModel::updateEnergyClass,
                label = { Text("Клас (A-G)") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = viewModel::addAudit) {
                Text("Додати аудит")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.loadAudits(forceRefresh = true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Синхронізувати з сервером")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(state.audits) { audit ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Адреса: ${audit.address}", style = MaterialTheme.typography.titleMedium)
                                Text("Споживання: ${audit.consumptionKwh} кВт·год")
                                Text("Клас енергоефективності: ${audit.energyClass}")
                                Text("Дата перевірки: ${audit.auditDate}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.deleteAudit(audit.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Видалити")
                            }
                        }
                    }
                }
            }
        }
    }
}