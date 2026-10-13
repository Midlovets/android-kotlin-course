package com.example.myapplication.model

/**
 * Перелік систем будівлі за замовчуванням.
 * Щоб додати нову систему, достатньо додати рядок сюди
 * (та іконку в ui/components/SystemIcons.kt) – решта UI побудується автоматично.
 */
object BuildingCatalog {
    val defaultSystems: List<BuildingSystem> = listOf(
        BuildingSystem(1, "Освітлення", SystemType.LIGHTING, 18.0),
        BuildingSystem(2, "Опалення", SystemType.HEATING, 55.0),
        BuildingSystem(3, "Вентиляція", SystemType.VENTILATION, 22.0),
        BuildingSystem(4, "Гаряча вода", SystemType.HOT_WATER, 15.0),
        BuildingSystem(5, "Оргтехніка", SystemType.EQUIPMENT, 30.0),
        BuildingSystem(6, "Ліфти", SystemType.ELEVATORS, 12.0)
    )
}
