package com.midlovets.pw_09_rpzmp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuditDao {
    @Query("SELECT * FROM building_audits ORDER BY id DESC")
    suspend fun getAllAudits(): List<BuildingAudit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudits(audits: List<BuildingAudit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: BuildingAudit)

    @Query("DELETE FROM building_audits WHERE id = :id")
    suspend fun deleteAudit(id: Int)

    @Query("DELETE FROM building_audits")
    suspend fun clearAll()
}