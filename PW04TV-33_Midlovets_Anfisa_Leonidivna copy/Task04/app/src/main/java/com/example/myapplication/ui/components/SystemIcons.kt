package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Elevator
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.model.SystemType

/** Відповідність типу системи та зображення (іконки). Зберігається в UI-шарі, а не в моделі. */
fun SystemType.icon(): ImageVector = when (this) {
    SystemType.LIGHTING -> Icons.Rounded.Lightbulb
    SystemType.HEATING -> Icons.Rounded.LocalFireDepartment
    SystemType.VENTILATION -> Icons.Rounded.Air
    SystemType.HOT_WATER -> Icons.Rounded.WaterDrop
    SystemType.EQUIPMENT -> Icons.Rounded.Computer
    SystemType.ELEVATORS -> Icons.Rounded.Elevator
}
