package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.MonthlyEnergy
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

/**
 * Стовпчаста діаграма витрат енергії по місяцях (стороння бібліотека Vico).
 *
 * Взаємодія зі State: коли змінюється список [monthly] (після нового розрахунку),
 * LaunchedEffect перезапускається й передає нові дані в modelProducer –
 * діаграма сама плавно анімує перехід до нових значень.
 */
@Composable
fun MonthlyEnergyChart(
    monthly: List<MonthlyEnergy>,
    modifier: Modifier = Modifier
) {
    // Джерело даних для діаграми; переживає рекомпозиції завдяки remember
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(monthly) {
        modelProducer.runTransaction {
            columnSeries { series(monthly.map { it.kwh }) }
        }
    }

    // Підписи осі X – назви місяців замість індексів 0, 1, 2...
    val monthLabels = remember(monthly) { monthly.map { it.month } }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    monthLabels.getOrElse(value.toInt()) { "" }
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}
