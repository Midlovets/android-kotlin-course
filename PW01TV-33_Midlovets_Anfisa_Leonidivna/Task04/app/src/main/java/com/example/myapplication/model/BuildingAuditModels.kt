package com.example.myapplication.model

/** Тип інженерної системи будівлі. Модель не залежить від UI: іконку обирає ui-шар. */
enum class SystemType { LIGHTING, HEATING, VENTILATION, HOT_WATER, EQUIPMENT, ELEVATORS }

/** Інженерна система будівлі, яку аналізує енергоаудит. */
data class BuildingSystem(
    val id: Int,
    val name: String,
    val type: SystemType,
    val ratedPowerKw: Double,       // номінальна потужність, кВт
    val isEnabled: Boolean = true   // чи працює система зараз
)

/** Режим роботи будівлі: коефіцієнт зменшує навантаження всіх працюючих систем. */
enum class OperatingMode(val title: String, val loadFactor: Double) {
    DAY("День", 1.0),
    NIGHT("Ніч", 0.35),
    WEEKEND("Вихідний", 0.55)
}

/** Рівень навантаження відносно заданого ліміту. */
enum class LoadLevel { NORMAL, WARNING, EXCEEDED }
