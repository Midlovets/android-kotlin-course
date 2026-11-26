package com.example.myapplication.logic

import com.example.myapplication.model.BuildingSystem
import com.example.myapplication.model.LoadLevel
import com.example.myapplication.model.OperatingMode
import java.util.Locale

/**
 * Бізнес-логіка екрана енергоаудиту. Усі функції «чисті» (без Android та Compose),
 * тому їх можна тестувати окремо й перевикористовувати в інших екранах.
 */
object AuditLogic {

    /** Частка ліміту, з якої навантаження вважається «наближеним до ліміту». */
    private const val WARNING_THRESHOLD = 0.8

    /** Поточна потужність однієї системи: вимкнена система нічого не споживає. */
    fun currentPowerKw(system: BuildingSystem, mode: OperatingMode): Double =
        if (system.isEnabled) system.ratedPowerKw * mode.loadFactor else 0.0

    /** Сумарна потужність усіх систем у вибраному режимі. */
    fun totalPowerKw(systems: List<BuildingSystem>, mode: OperatingMode): Double =
        systems.sumOf { currentPowerKw(it, mode) }

    fun activeCount(systems: List<BuildingSystem>): Int = systems.count { it.isEnabled }

    /** Рівень навантаження відносно ліміту: до 80% – норма, 80–100% – увага, понад 100% – перевищення. */
    fun loadLevel(totalKw: Double, limitKw: Double): LoadLevel {
        val ratio = totalKw / limitKw
        return when {
            ratio > 1.0 -> LoadLevel.EXCEEDED
            ratio >= WARNING_THRESHOLD -> LoadLevel.WARNING
            else -> LoadLevel.NORMAL
        }
    }

    /** Частка ліміту для прогрес-індикатора (обмежена діапазоном 0..1). */
    fun loadRatio(totalKw: Double, limitKw: Double): Float =
        (totalKw / limitKw).toFloat().coerceIn(0f, 1f)

    /** Найбільший споживач серед увімкнених систем (або null, якщо все вимкнено). */
    fun topConsumer(systems: List<BuildingSystem>, mode: OperatingMode): BuildingSystem? =
        systems.filter { it.isEnabled }.maxByOrNull { currentPowerKw(it, mode) }

    /** Повертає новий список id увімкнених систем: якщо id був – прибирає, якщо не було – додає. */
    fun toggleEnabled(enabledIds: List<Int>, id: Int): List<Int> =
        if (id in enabledIds) enabledIds - id else enabledIds + id

    /** Перетворює текст поля «Ліміт» на число (кома або крапка); некоректне значення дає null. */
    fun parseLimitKw(text: String): Double? {
        val value = text.trim().replace(',', '.').toDoubleOrNull() ?: return null
        return if (value > 0.0 && value <= MAX_LIMIT_KW) value else null
    }

    /** Текстова рекомендація аудитора залежно від рівня навантаження. */
    fun auditComment(level: LoadLevel?, topConsumer: BuildingSystem?): String {
        val consumerNote = topConsumer?.let { " Найбільший споживач: ${it.name}." }.orEmpty()
        return when (level) {
            null -> "Введіть коректний ліміт потужності – додатне число в кВт."
            LoadLevel.NORMAL -> "Навантаження в межах норми, режим роботи енергоефективний.$consumerNote"
            LoadLevel.WARNING -> "Навантаження наближається до ліміту.$consumerNote"
            LoadLevel.EXCEEDED ->
                "Ліміт перевищено! Вимкніть частину систем або змініть режим.$consumerNote"
        }
    }

    fun formatKw(value: Double): String = String.format(Locale.getDefault(), "%.1f", value)

    private const val MAX_LIMIT_KW = 10_000.0
}
