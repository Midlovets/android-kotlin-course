package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.logic.AuditLogic
import com.example.myapplication.model.BuildingCatalog
import com.example.myapplication.model.OperatingMode
import com.example.myapplication.ui.components.AuditHeader
import com.example.myapplication.ui.components.LimitTextField
import com.example.myapplication.ui.components.ModeSelector
import com.example.myapplication.ui.components.SummaryCard
import com.example.myapplication.ui.components.SystemCard
import com.example.myapplication.ui.theme.EnergyAuditTheme

private const val DEFAULT_LIMIT = "160"

/**
 * Головний екран: єдине місце, де живе стан (State).
 *
 * Потік даних:
 *  1. Користувач змінює назву, ліміт, режим або вмикає/вимикає систему –
 *     відповідна State-змінна отримує нове значення.
 *  2. Compose перевикликає екран (рекомпозиція), і всі похідні значення
 *     (сумарна потужність, рівень, коментар) обчислюються заново функціями з AuditLogic.
 *  3. Компоненти лише відображають передані дані й повідомляють екран про події.
 *
 * Адаптивність: LazyVerticalGrid з GridCells.Adaptive сам обирає кількість колонок
 * залежно від ширини екрана (на телефоні – 2, на планшеті – більше).
 */
@Composable
fun BuildingAuditScreen(modifier: Modifier = Modifier) {
    // --- State (rememberSaveable переживає поворот екрана) ---
    var buildingName by rememberSaveable { mutableStateOf("Навчальний корпус №1") }
    var limitText by rememberSaveable { mutableStateOf(DEFAULT_LIMIT) }
    var mode by rememberSaveable { mutableStateOf(OperatingMode.DAY) }
    var enabledIds by rememberSaveable {
        mutableStateOf(BuildingCatalog.defaultSystems.map { it.id })
    }

    // --- Похідні значення: перераховуються автоматично при зміні State ---
    val systems = remember(enabledIds) {
        BuildingCatalog.defaultSystems.map { it.copy(isEnabled = it.id in enabledIds) }
    }
    val totalKw = AuditLogic.totalPowerKw(systems, mode)
    val limitKw = AuditLogic.parseLimitKw(limitText)
    val level = limitKw?.let { AuditLogic.loadLevel(totalKw, it) }
    val ratio = limitKw?.let { AuditLogic.loadRatio(totalKw, it) } ?: 0f
    val comment = AuditLogic.auditComment(level, AuditLogic.topConsumer(systems, mode))

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Верхні блоки займають усю ширину сітки
        item(span = { GridItemSpan(maxLineSpan) }) {
            AuditHeader(
                buildingName = buildingName,
                onBuildingNameChange = { buildingName = it }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LimitTextField(
                value = limitText,
                onValueChange = { limitText = it },
                isError = limitKw == null
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            ModeSelector(selected = mode, onSelect = { mode = it })
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            SummaryCard(
                title = buildingName.ifBlank { "Поточне навантаження" },
                totalKw = totalKw,
                level = level,
                ratio = ratio,
                activeCount = AuditLogic.activeCount(systems),
                totalCount = systems.size,
                comment = comment
            )
        }

        // Картки систем розташовуються в адаптивній сітці
        items(systems, key = { it.id }) { system ->
            SystemCard(
                system = system,
                powerKw = AuditLogic.currentPowerKw(system, mode),
                onToggle = { enabledIds = AuditLogic.toggleEnabled(enabledIds, system.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BuildingAuditScreenPreview() {
    EnergyAuditTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BuildingAuditScreen()
        }
    }
}
