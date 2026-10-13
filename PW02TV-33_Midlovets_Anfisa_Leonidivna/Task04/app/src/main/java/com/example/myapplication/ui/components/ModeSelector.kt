package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.OperatingMode

/** Ряд кнопок вибору режиму: вибрана кнопка залита, решта – з контуром. */
@Composable
fun ModeSelector(
    selected: OperatingMode,
    onSelect: (OperatingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OperatingMode.entries.forEach { mode ->
            val buttonModifier = Modifier
                .weight(1f)
                .height(48.dp)
            if (mode == selected) {
                Button(
                    onClick = { onSelect(mode) },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(14.dp)
                ) { Text(mode.title) }
            } else {
                OutlinedButton(
                    onClick = { onSelect(mode) },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(14.dp)
                ) { Text(mode.title) }
            }
        }
    }
}
