package com.example.myapplication.logic

/** Один місяць опалювального сезону: середня зовнішня температура та кількість опалювальних діб. */
data class HeatingMonth(
    val name: String,
    val avgOutdoorTempC: Double,
    val heatingDays: Int
)

/**
 * Кліматичні дані для навчального розрахунку (наближені значення для Києва).
 * Винесені в окремий файл, щоб для іншого міста достатньо було замінити таблицю.
 */
object ClimateData {

    /** Розрахункова зовнішня температура для визначення пікової потужності тепловтрат, °C. */
    const val DESIGN_OUTDOOR_TEMP_C = -22.0

    /** Опалювальний сезон: жовтень і квітень враховані наполовину (≈ 181 доба). */
    val heatingSeason: List<HeatingMonth> = listOf(
        HeatingMonth("Жов", 7.0, 15),
        HeatingMonth("Лис", 2.0, 30),
        HeatingMonth("Гру", -2.0, 31),
        HeatingMonth("Січ", -4.0, 31),
        HeatingMonth("Лют", -3.0, 28),
        HeatingMonth("Бер", 1.0, 31),
        HeatingMonth("Кві", 7.0, 15)
    )
}
