package com.example.vidyarthi_bus.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String = "",
    val routeId: String = "",
    val userId: String = "",
    val crowdLevel: CrowdLevel = CrowdLevel.EMPTY,
    val timestamp: Long = System.currentTimeMillis()
)