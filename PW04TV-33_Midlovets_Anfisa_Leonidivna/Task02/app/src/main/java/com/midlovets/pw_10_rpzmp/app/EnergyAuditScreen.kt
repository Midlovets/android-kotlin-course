package com.midlovets.pw_10_rpzmp.app


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnergyAuditScreen(viewModel: AuditViewModel = viewModel()) {
    val audits by viewModel.audits.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var filterQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Енергоаудит") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // Відображення агрегованої статистики
            stats?.let { s ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Загальна статистика", style = MaterialTheme.typography.titleMedium)
                        Text("Будівель в базі: ${s.totalBuildings}")
                        Text("Сер. споживання: ${s.averageConsumption} кВт·год")
                        Text("Топ клас: ${s.topEnergyClass}")
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = {
                        filterQuery = it
                        viewModel.filterByEnergyClass(it)
                    },
                    label = { Text("Фільтр за класом (напр. A)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.loadData() }) {
                    Text("Оновити")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && audits.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            LazyColumn {
                items(audits) { audit ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(audit.address, style = MaterialTheme.typography.titleMedium)
                            Text("Клас: ${audit.energyClass} | ${audit.consumptionKwh} кВт·год")

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Рекомендації:", style = MaterialTheme.typography.labelLarge)
                            audit.recommendations.forEach { rec ->
                                Text("• $rec", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}