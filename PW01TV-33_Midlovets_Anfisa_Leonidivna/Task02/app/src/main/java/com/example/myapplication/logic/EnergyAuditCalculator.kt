package com.example.myapplication.logic

import com.example.myapplication.model.EnergyAuditInput
import com.example.myapplication.model.EnergyAuditResult
import com.example.myapplication.model.EnergyClass
import com.example.myapplication.model.MonthlyEnergy

/**
 * Чиста бізнес-логіка енергоаудиту (без залежностей від Android та Compose),
 * тому її легко тестувати та перевикористовувати.
 *
 * Спрощена модель: враховуються лише трансмісійні тепловтрати через огородження.
 *   Q (Вт)      = U × A × ΔT
 *   E (кВт·год) = Q × 24 × кількість діб / 1000
 */
object EnergyAuditCalculator {

    private const val HOURS_PER_DAY = 24
    private const val WATTS_IN_KILOWATT = 1000.0

    /** Потужність тепловтрат у кВт: U × A × ΔT / 1000. */
    fun heatLossPowerKw(uValue: Double, areaM2: Double, deltaTempK: Double): Double =
        uValue * areaM2 * deltaTempK / WATTS_IN_KILOWATT

    /** Умовна шкала класів за питомими витратами (кВт·год/м² на рік) – для навчального прикладу. */
    fun classify(specificKwhPerM2: Double): EnergyClass = when {
        specificKwhPerM2 <= 25.0 -> EnergyClass.A
        specificKwhPerM2 <= 50.0 -> EnergyClass.B
        specificKwhPerM2 <= 75.0 -> EnergyClass.C
        specificKwhPerM2 <= 100.0 -> EnergyClass.D
        specificKwhPerM2 <= 130.0 -> EnergyClass.E
        specificKwhPerM2 <= 170.0 -> EnergyClass.F
        else -> EnergyClass.G
    }

    /** Повний розрахунок за перевіреними вхідними даними. */
    fun calculate(input: EnergyAuditInput): EnergyAuditResult {
        // 1. Витрати енергії по кожному місяцю опалювального сезону
        val monthly = ClimateData.heatingSeason.map { month ->
            val deltaT = (input.indoorTempC - month.avgOutdoorTempC).coerceAtLeast(0.0)
            val powerKw = heatLossPowerKw(input.uValue, input.envelopeAreaM2, deltaT)
            MonthlyEnergy(
                month = month.name,
                kwh = powerKw * HOURS_PER_DAY * month.heatingDays
            )
        }

        // 2. Підсумки за сезон
        val seasonEnergy = monthly.sumOf { it.kwh }
        val specific = seasonEnergy / input.heatedAreaM2

        // 3. Пікова потужність при розрахунковій температурі
        val peakLoss = heatLossPowerKw(
            uValue = input.uValue,
            areaM2 = input.envelopeAreaM2,
            deltaTempK = input.indoorTempC - ClimateData.DESIGN_OUTDOOR_TEMP_C
        )

        return EnergyAuditResult(
            peakLossKw = peakLoss,
            seasonEnergyKwh = seasonEnergy,
            specificKwhPerM2 = specific,
            seasonCostUah = seasonEnergy * input.tariffUahPerKwh,
            energyClass = classify(specific),
            monthly = monthly
        )
    }
}
