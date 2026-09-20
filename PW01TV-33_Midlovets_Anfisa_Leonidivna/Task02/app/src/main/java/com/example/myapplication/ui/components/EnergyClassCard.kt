package com.example.myapplication.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.logic.formatNumber
import com.example.myapplication.model.EnergyClass

/** Велика картка класу енергоефективності. Колір плавно змінюється разом із класом. */
@Composable
fun EnergyClassCard(
    energyClass: EnergyClass,
    specificKwhPerM2: Double,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val classColor by animateColorAsState(
        targetValue = when (energyClass) {
            EnergyClass.A, EnergyClass.B -> colors.secondary
            EnergyClass.C, EnergyClass.D -> colors.tertiary
            EnergyClass.E, EnergyClass.F, EnergyClass.G -> colors.error
        },
        animationSpec = tween(durationMillis = 700),
        label = "classColor"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = classColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = energyClass, label = "energyClass") { animatedClass ->
                Text(
                    text = animatedClass.name,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = classColor
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "Клас енергоефективності",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
                )
                Text(
                    text = "${formatNumber(specificKwhPerM2, 1)} кВт·год/м² на рік",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}
