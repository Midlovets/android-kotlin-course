package com.example.myapplication.model

/**
 * Поля форми. Використовуються, щоб прив'язати повідомлення про помилку
 * до конкретного текстового поля (див. EnergyAuditValidator та ParamTextField).
 */
enum class AuditField { ENVELOPE_AREA, U_VALUE, HEATED_AREA, INDOOR_TEMP, TARIFF }

/** Коректні (вже перевірені й перетворені в числа) вхідні параметри будівлі. */
data class EnergyAuditInput(
    val envelopeAreaM2: Double,     // площа огороджувальних конструкцій, м²
    val uValue: Double,             // коефіцієнт теплопередачі U, Вт/(м²·К)
    val heatedAreaM2: Double,       // опалювана площа, м²
    val indoorTempC: Double,        // температура всередині, °C
    val tariffUahPerKwh: Double     // тариф на теплову енергію, грн/кВт·год
)

/** Клас енергоефективності: A – найкращий, G – найгірший. */
enum class EnergyClass { A, B, C, D, E, F, G }

/** Витрати теплової енергії за один місяць опалювального сезону (для графіка). */
data class MonthlyEnergy(val month: String, val kwh: Double)

/** Результат розрахунку, який відображається в UI. */
data class EnergyAuditResult(
    val peakLossKw: Double,             // потужність тепловтрат при розрахунковій температурі, кВт
    val seasonEnergyKwh: Double,        // витрати енергії за сезон, кВт·год
    val specificKwhPerM2: Double,       // питомі витрати, кВт·год/м² на рік
    val seasonCostUah: Double,          // вартість опалення за сезон, грн
    val energyClass: EnergyClass,
    val monthly: List<MonthlyEnergy>
)

/** Результат валідації форми: або готові дані, або помилки по полях. */
sealed interface ValidationResult {
    data class Valid(val input: EnergyAuditInput) : ValidationResult
    data class Invalid(val errors: Map<AuditField, String>) : ValidationResult
}
