package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Заголовок екрана: зображення будівлі, назва застосунку та текстове поле з назвою об'єкта. */
@Composable
fun AuditHeader(
    buildingName: String,
    onBuildingNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = colors.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                // Зображення будівлі (векторне), пофарбоване кольором теми
                Image(
                    imageVector = Icons.Rounded.Apartment,
                    contentDescription = "Будівля",
                    colorFilter = ColorFilter.tint(colors.onPrimaryContainer),
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Енергоаудит будівель",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary
                )
                Text(
                    text = "Системи будівлі",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = colors.onBackground
                )
            }
        }

        // Текстове поле №1: назва об'єкта
        OutlinedTextField(
            value = buildingName,
            onValueChange = onBuildingNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Назва будівлі") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
