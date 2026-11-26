package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.logic.AuditLogic
import com.example.myapplication.model.BuildingSystem

/**
 * Картка однієї інженерної системи: зображення, назва, поточна потужність, статус і кнопка.
 * Stateless: сама картка стану не зберігає, а лише викликає onToggle.
 */
@Composable
fun SystemCard(
    system: BuildingSystem,
    powerKw: Double,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val containerColor by animateColorAsState(
        targetValue = if (system.isEnabled) colors.surface else colors.surfaceVariant,
        label = "cardColor"
    )
    val accent = if (system.isEnabled) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.5f)

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.3f)),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                imageVector = system.type.icon(),
                contentDescription = system.name,
                colorFilter = ColorFilter.tint(accent),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = system.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Text(
                text = "${AuditLogic.formatKw(powerKw)} кВт",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = colors.onSurface
            )
            Text(
                text = if (system.isEnabled) "Працює" else "Вимкнено",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (system.isEnabled) "Вимкнути" else "Увімкнути")
            }
        }
    }
}
