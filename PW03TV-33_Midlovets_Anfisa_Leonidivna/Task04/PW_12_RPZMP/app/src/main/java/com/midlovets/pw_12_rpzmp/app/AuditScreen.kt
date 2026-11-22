package com.midlovets.pw_12_rpzmp.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(viewModel: AuditErrorViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Енергоаудит (Обробка помилок)") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = { viewModel.fetchAuditsWithRetry(errorCodeToSimulate = null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Завантажити аудити (Успіх)")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.fetchAuditsWithRetry(errorCodeToSimulate = 404) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Симулювати 404 (Not Found)")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.fetchAuditsWithRetry(errorCodeToSimulate = 500) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Симулювати 500 (З Retry-логікою)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.audits?.let { audits ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(audits) { audit ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Адреса: ${audit.address}", style = MaterialTheme.typography.titleMedium)
                                Text("Клас: ${audit.energyClass}")
                            }
                        }
                    }
                }
            }
        }
    }
}