package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.SquareFoot
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.logic.EnergyAuditCalculator
import com.example.myapplication.logic.EnergyAuditValidator
import com.example.myapplication.logic.formatNumber
import com.example.myapplication.model.AuditField
import com.example.myapplication.model.EnergyAuditResult
import com.example.myapplication.model.ValidationResult
import com.example.myapplication.ui.components.EnergyClassCard
import com.example.myapplication.ui.components.MonthlyEnergyChart
import com.example.myapplication.ui.components.ParamTextField
import com.example.myapplication.ui.components.ResultCard
import com.example.myapplication.ui.components.SectionCard
import com.example.myapplication.ui.theme.EnergyAuditTheme

// Значення за замовчуванням: форма одразу готова до розрахунку
private const val DEFAULT_ENVELOPE_AREA = "850"
private const val DEFAULT_U_VALUE = "0.6"
private const val DEFAULT_HEATED_AREA = "1200"
private const val DEFAULT_INDOOR_TEMP = "20"
private const val DEFAULT_TARIFF = "2.0"

/**
 * Екран розрахунку тепловтрат будівлі (просунутий рівень).
 *
 * Потік даних (UI ↔ State ↔ логіка):
 *  1. Користувач вводить текст → onValueChange змінює State-змінну → поле перемальовується.
 *  2. Натискання «Розрахувати» → EnergyAuditValidator перевіряє рядки.
 *  3. Valid   → EnergyAuditCalculator рахує результат → result = ... → з'являється блок результатів.
 *     Invalid → errors = ... → помилки відображаються під відповідними полями.
 *  Екран лише зберігає стан і викликає логіку; самі обчислення живуть у пакеті logic.
 */
@Composable
fun EnergyAuditScreen(modifier: Modifier = Modifier) {
    // --- State введення (rememberSaveable переживає поворот екрана) ---
    var envelopeArea by rememberSaveable { mutableStateOf(DEFAULT_ENVELOPE_AREA) }
    var uValue by rememberSaveable { mutableStateOf(DEFAULT_U_VALUE) }
    var heatedArea by rememberSaveable { mutableStateOf(DEFAULT_HEATED_AREA) }
    var indoorTemp by rememberSaveable { mutableStateOf(DEFAULT_INDOOR_TEMP) }
    var tariff by rememberSaveable { mutableStateOf(DEFAULT_TARIFF) }

    // --- State результату та помилок ---
    var errors by remember { mutableStateOf<Map<AuditField, String>>(emptyMap()) }
    var result by remember { mutableStateOf<EnergyAuditResult?>(null) }

    val onCalculate: () -> Unit = {
        when (val validation = EnergyAuditValidator.validate(
            envelopeArea, uValue, heatedArea, indoorTemp, tariff
        )) {
            is ValidationResult.Valid -> {
                errors = emptyMap()
                result = EnergyAuditCalculator.calculate(validation.input)
            }
            is ValidationResult.Invalid -> {
                errors = validation.errors
                result = null
            }
        }
    }

    val onReset: () -> Unit = {
        envelopeArea = DEFAULT_ENVELOPE_AREA
        uValue = DEFAULT_U_VALUE
        heatedArea = DEFAULT_HEATED_AREA
        indoorTemp = DEFAULT_INDOOR_TEMP
        tariff = DEFAULT_TARIFF
        errors = emptyMap()
        result = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader()

        SectionCard(title = "Параметри будівлі") {
            ParamTextField(
                label = "Площа огороджень",
                value = envelopeArea,
                onValueChange = { envelopeArea = it },
                unit = "м²",
                error = errors[AuditField.ENVELOPE_AREA],
                modifier = Modifier.fillMaxWidth()
            )
            ParamTextField(
                label = "Коефіцієнт теплопередачі U",
                value = uValue,
                onValueChange = { uValue = it },
                unit = "Вт/(м²·К)",
                error = errors[AuditField.U_VALUE],
                modifier = Modifier.fillMaxWidth()
            )
            ParamTextField(
                label = "Опалювана площа",
                value = heatedArea,
                onValueChange = { heatedArea = it },
                unit = "м²",
                error = errors[AuditField.HEATED_AREA],
                modifier = Modifier.fillMaxWidth()
            )
            ParamTextField(
                label = "Температура всередині",
                value = indoorTemp,
                onValueChange = { indoorTemp = it },
                unit = "°C",
                error = errors[AuditField.INDOOR_TEMP],
                modifier = Modifier.fillMaxWidth()
            )
            ParamTextField(
                label = "Тариф на тепло",
                value = tariff,
                onValueChange = { tariff = it },
                unit = "грн/кВт·год",
                error = errors[AuditField.TARIFF],
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onCalculate,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Розрахувати", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Скинути")
            }
        }

        // Блок результатів плавно з'являється/зникає залежно від State
        AnimatedVisibility(
            visible = result != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            result?.let { ResultSection(it) }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Apartment,
                contentDescription = "Будівля",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Енергоаудит будівель",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Розрахунок тепловтрат",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/** Блок результатів: клас, чотири показники та діаграма по місяцях. */
@Composable
private fun ResultSection(result: EnergyAuditResult) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        EnergyClassCard(
            energyClass = result.energyClass,
            specificKwhPerM2 = result.specificKwhPerM2
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResultCard(
                title = "Тепловтрати",
                value = formatNumber(result.peakLossKw, 1),
                unit = "кВт при −22 °C",
                icon = Icons.Rounded.Thermostat,
                modifier = Modifier.weight(1f)
            )
            ResultCard(
                title = "Енергія за сезон",
                value = formatNumber(result.seasonEnergyKwh),
                unit = "кВт·год",
                icon = Icons.Rounded.Bolt,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResultCard(
                title = "Питомі витрати",
                value = formatNumber(result.specificKwhPerM2, 1),
                unit = "кВт·год/м²",
                icon = Icons.Rounded.SquareFoot,
                modifier = Modifier.weight(1f)
            )
            ResultCard(
                title = "Вартість",
                value = formatNumber(result.seasonCostUah),
                unit = "грн за сезон",
                icon = Icons.Rounded.Payments,
                modifier = Modifier.weight(1f)
            )
        }

        SectionCard(title = "Витрати енергії по місяцях (кВт·год)") {
            MonthlyEnergyChart(monthly = result.monthly)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EnergyAuditScreenPreview() {
    EnergyAuditTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EnergyAuditScreen()
        }
    }
}
