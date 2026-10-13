package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.EnergyAuditTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Верхні межі шкал для прогрес-індикаторів
private const val MAX_CONSUMPTION_KWH = 500
private const val MAX_HEAT_LOSS_PERCENT = 60

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnergyAuditTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnergyAuditScreen()
                }
            }
        }
    }
}

/**
 * Stateful-обгортка: весь стан екрана (State у Compose) живе тут.
 * rememberSaveable зберігає значення, наприклад, при повороті екрана.
 */
@Composable
fun EnergyAuditScreen() {
    var consumptionKwh by rememberSaveable { mutableIntStateOf(randomConsumption()) }
    var heatLossPercent by rememberSaveable { mutableIntStateOf(randomHeatLoss()) }
    var updatedAt by rememberSaveable { mutableStateOf(currentTime()) }

    EnergyAuditContent(
        consumptionKwh = consumptionKwh,
        heatLossPercent = heatLossPercent,
        energyClass = energyClassFor(consumptionKwh),
        updatedAt = updatedAt,
        onRefresh = {
            // Імітація нових показань лічильників
            consumptionKwh = randomConsumption()
            heatLossPercent = randomHeatLoss()
            updatedAt = currentTime()
        }
    )
}

/** Stateless UI: отримує дані та callback, тому легко перевикористовується й має Preview. */
@Composable
fun EnergyAuditContent(
    consumptionKwh: Int,
    heatLossPercent: Int,
    energyClass: String,
    updatedAt: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок + клас енергоефективності
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Енергоаудит",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "будівель",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            EnergyClassBadge(energyClass = energyClass)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Sync,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Останнє оновлення: $updatedAt",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Картки показників
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            MetricCard(
                title = "Споживання електроенергії",
                icon = Icons.Rounded.Bolt,
                value = consumptionKwh,
                maxValue = MAX_CONSUMPTION_KWH,
                unit = "кВт·год/добу",
                modifier = Modifier.fillMaxWidth()
            )
            MetricCard(
                title = "Тепловтрати будівлі",
                icon = Icons.Rounded.Thermostat,
                value = heatLossPercent,
                maxValue = MAX_HEAT_LOSS_PERCENT,
                unit = "%",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Sync,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Оновити показники аудиту",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Бейдж класу енергоефективності (A–G) зі зміною літери та кольору при оновленні. */
@Composable
private fun EnergyClassBadge(energyClass: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val classColor by animateColorAsState(
        targetValue = when (energyClass) {
            "A", "B" -> colors.secondary
            "C", "D" -> colors.tertiary
            else -> colors.error
        },
        animationSpec = tween(durationMillis = 700),
        label = "classColor"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = classColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Клас",
                style = MaterialTheme.typography.labelLarge,
                color = classColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            AnimatedContent(targetState = energyClass, label = "energyClass") { animatedClass ->
                Text(
                    text = animatedClass,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = classColor
                )
            }
        }
    }
}

/**
 * Картка одного показника. Анімації:
 *  - AnimatedContent: число «виїжджає» вгору/вниз залежно від напряму зміни;
 *  - animateFloatAsState: плавний рух смуги прогресу;
 *  - animateColorAsState: плавна зміна кольору (норма → підвищене → критичне).
 */
@Composable
private fun MetricCard(
    title: String,
    icon: ImageVector,
    value: Int,
    maxValue: Int,
    unit: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val progress = value / maxValue.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    val indicatorColor by animateColorAsState(
        targetValue = when {
            progress < 0.5f -> colors.primary
            progress < 0.8f -> colors.tertiary
            else -> colors.error
        },
        animationSpec = tween(durationMillis = 700),
        label = "indicatorColor"
    )
    val statusText = when {
        progress < 0.5f -> "Норма"
        progress < 0.8f -> "Підвищене"
        else -> "Критичне"
    }
    val statusBg by animateColorAsState(
        targetValue = indicatorColor.copy(alpha = 0.15f),
        label = "statusBg"
    )

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.3f)),
        colors = CardDefaults.outlinedCardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surfaceVariant,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = value,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                    (slideOutVertically { -it } + fadeOut())
                        } else {
                            (slideInVertically { -it } + fadeIn()) togetherWith
                                    (slideOutVertically { it } + fadeOut())
                        }
                    },
                    label = "valueChange"
                ) { animatedValue ->
                    Text(
                        text = animatedValue.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusText,
                        color = indicatorColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = indicatorColor,
                trackColor = colors.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

/** Клас енергоефективності за добовим споживанням (спрощена шкала для навчального прикладу). */
private fun energyClassFor(kwhPerDay: Int): String = when {
    kwhPerDay < 100 -> "A"
    kwhPerDay < 160 -> "B"
    kwhPerDay < 230 -> "C"
    kwhPerDay < 300 -> "D"
    kwhPerDay < 380 -> "E"
    kwhPerDay < 440 -> "F"
    else -> "G"
}

private fun randomConsumption(): Int = Random.nextInt(60, MAX_CONSUMPTION_KWH + 1)

private fun randomHeatLoss(): Int = Random.nextInt(5, MAX_HEAT_LOSS_PERCENT)

private fun currentTime(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

@Preview(showBackground = true)
@Composable
private fun EnergyAuditPreview() {
    EnergyAuditTheme(darkTheme = true) {
        EnergyAuditContent(
            consumptionKwh = 245,
            heatLossPercent = 32,
            energyClass = "D",
            updatedAt = "12:00:00",
            onRefresh = {}
        )
    }
}