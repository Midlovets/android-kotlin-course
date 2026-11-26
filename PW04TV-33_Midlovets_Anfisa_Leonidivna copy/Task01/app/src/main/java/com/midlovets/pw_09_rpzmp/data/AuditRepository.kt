package com.midlovets.pw_09_rpzmp.data

class AuditRepository(
    private val localDao: AuditDao,
    private val remoteApi: AuditApiService
) {
    suspend fun getAudits(forceRefresh: Boolean = false): Result<List<BuildingAudit>> {
        return try {
            if (forceRefresh) {
                val remoteData = remoteApi.getAudits()
                localDao.clearAll()
                localDao.insertAudits(remoteData)
                Result.success(remoteData)
            } else {
                val localData = localDao.getAllAudits()
                if (localData.isNotEmpty()) {
                    Result.success(localData)
                } else {
                    val remoteData = remoteApi.getAudits()
                    localDao.insertAudits(remoteData)
                    Result.success(remoteData)
                }
            }
        } catch (e: Exception) {
            val localData = localDao.getAllAudits()
            if (localData.isNotEmpty()) Result.success(localData)
            else Result.failure(e)
        }
    }

    suspend fun addAudit(audit: BuildingAudit): Result<BuildingAudit> {
        return try {
            val response = remoteApi.addAudit(audit)
            if (response.isSuccessful && response.body() != null) {
                localDao.insertAudit(response.body()!!)
                Result.success(response.body()!!)
            } else {
                localDao.insertAudit(audit)
                Result.success(audit)
            }
        } catch (e: Exception) {
            localDao.insertAudit(audit)
            Result.success(audit)
        }
    }

    suspend fun deleteAudit(id: Int): Result<Boolean> {
        return try {
            remoteApi.deleteAudit(id)
            localDao.deleteAudit(id)
            Result.success(true)
        } catch (e: Exception) {
            localDao.deleteAudit(id)
            Result.success(true)
        }
    }
}