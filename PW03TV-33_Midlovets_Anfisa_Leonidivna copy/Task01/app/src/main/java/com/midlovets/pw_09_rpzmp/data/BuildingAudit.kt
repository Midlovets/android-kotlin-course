package com.midlovets.pw_09_rpzmp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "building_audits")
data class BuildingAudit(
    @PrimaryKey val id: Int,
    @SerializedName("address") val address: String,
    @SerializedName("consumption_kwh") val consumptionKwh: Double,
    @SerializedName("energy_class") val energyClass: String,
    @SerializedName("audit_date") val auditDate: String
)
