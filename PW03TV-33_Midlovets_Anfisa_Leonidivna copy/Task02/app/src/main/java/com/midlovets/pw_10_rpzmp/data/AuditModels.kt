package com.midlovets.pw_10_rpzmp.data

import com.google.gson.annotations.SerializedName

data class BuildingAudit(
    val id: Int,
    val address: String,
    @SerializedName("energy_class") val energyClass: String,
    @SerializedName("consumption_kwh") val consumptionKwh: Double,
    val recommendations: List<String>
)

data class AuditStats(
    @SerializedName("total_buildings") val totalBuildings: Int,
    @SerializedName("avg_consumption") val averageConsumption: Double,
    @SerializedName("top_energy_class") val topEnergyClass: String
)