package com.example.myapplication.logic

import com.example.myapplication.model.AuditField
import com.example.myapplication.model.EnergyAuditInput
import com.example.myapplication.model.ValidationResult

/**
 * Перевірка введених користувачем рядків. Повертає або готовий EnergyAuditInput,
 * або мапу «поле → текст помилки», яку UI показує під відповідним полем.
 */
object EnergyAuditValidator {

    fun validate(
        envelopeArea: String,
        uValue: String,
        heatedArea: String,
        indoorTemp: String,
        tariff: String
    ): ValidationResult {
        val errors = mutableMapOf<AuditField, String>()

        val area = parse(envelopeArea, 1.0, 100_000.0, AuditField.ENVELOPE_AREA, errors)
        val u = parse(uValue, 0.05, 6.0, AuditField.U_VALUE, errors)
        val heated = parse(heatedArea, 1.0, 100_000.0, AuditField.HEATED_AREA, errors)
        val indoor = parse(indoorTemp, 10.0, 30.0, AuditField.INDOOR_TEMP, errors)
        val price = parse(tariff, 0.1, 100.0, AuditField.TARIFF, errors)

        if (area == null || u == null || heated == null || indoor == null || price == null) {
            return ValidationResult.Invalid(errors)
        }
        return ValidationResult.Valid(EnergyAuditInput(area, u, heated, indoor, price))
    }

    /** Перетворює рядок на число (кома або крапка) і перевіряє діапазон. */
    private fun parse(
        raw: String,
        min: Double,
        max: Double,
        field: AuditField,
        errors: MutableMap<AuditField, String>
    ): Double? {
        val value = raw.trim().replace(',', '.').toDoubleOrNull()
        return when {
            raw.isBlank() -> {
                errors[field] = "Введіть значення"
                null
            }
            value == null -> {
                errors[field] = "Некоректне число"
                null
            }
            value !in min..max -> {
                errors[field] = "Допустимо від $min до $max"
                null
            }
            else -> value
        }
    }
}
