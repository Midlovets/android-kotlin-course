package com.midlovets.pw_12_rpzmp.app

import com.midlovets.pw_12_rpzmp.data.BuildingAudit

data class AuditUiState(
    val audits: List<BuildingAudit>? = null,
    val isLoading: Boolean = false
)