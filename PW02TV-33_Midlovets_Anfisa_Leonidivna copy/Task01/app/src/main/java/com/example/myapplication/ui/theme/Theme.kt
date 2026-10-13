package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1B6EF3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = Color(0xFF001945),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color(0xFF2B1700),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    background = Color(0xFFF5F8FF),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE3E9F5),
    onSurfaceVariant = Color(0xFF44474F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    onPrimary = Color(0xFF002D6E),
    primaryContainer = Color(0xFF0F4CB8),
    onPrimaryContainer = Color(0xFFD9E2FF),
    secondary = Color(0xFF4DD0C4),
    onSecondary = Color(0xFF003732),
    tertiary = Color(0xFFFFC252),
    onTertiary = Color(0xFF442B00),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5C0A0A),
    background = Color(0xFF101318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF181C22),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF262B33),
    onSurfaceVariant = Color(0xFFC4C6D0)
)

/**
 * Material 3 тема застосунку «Енергоаудит будівель».
 * Світла/темна схема обирається автоматично за налаштуваннями системи.
 */
@Composable
fun EnergyAuditTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}