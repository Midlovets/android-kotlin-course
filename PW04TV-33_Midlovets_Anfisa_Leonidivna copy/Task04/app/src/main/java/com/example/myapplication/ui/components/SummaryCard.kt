package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.logic.AuditLogic
import com.example.myapplication.model.LoadLevel

/**
 * Підсумкова картка: сумарна потужність, статус відносно ліміту, прогрес та коментар аудитора.
 * Колір і смуга прогресу плавно змінюються при зміні стану.
 */
@Composable
fun SummaryCard(
    title: String,
    totalKw: Double,
    level: LoadLevel?,
    ratio: Float,
    activeCount: Int,
    totalCount: Int,
    comment: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val levelColor by animateColorAsState(
        targetValue = when (level) {
            LoadLevel.NORMAL -> colors.secondary
            LoadLevel.WARNING -> colors.tertiary
            LoadLevel.EXCEEDED -> colors.error
            null -> colors.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 600),
        label = "levelColor"
    )
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 600),
        label = "ratio"
    )
    val statusText = when (level) {
        LoadLevel.NORMAL -> "Норма"
        LoadLevel.WARNING -> "Увага"
        LoadLevel.EXCEEDED -> "Перевищено"
        null -> "—"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = levelColor.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = AuditLogic.formatKw(totalKw),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "кВт",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = levelColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = levelColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = levelColor,
                trackColor = colors.surface,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Активних систем: $activeCount з $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Text(
                text = comment,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
    }
}
