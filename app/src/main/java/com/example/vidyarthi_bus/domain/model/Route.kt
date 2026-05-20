package com.example.vidyarthi_bus.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String = "",
    val name: String = "",
    val from: String = "",
    val to: String = "",
    val departureTime: String = "",
    val village: String = "",
    val isActive: Boolean = true,
    val lastCrowdLevel: CrowdLevel = CrowdLevel.EMPTY,
    val lastReportTime: Long = 0L
)

enum class CrowdLevel {
    EMPTY, HALF_FULL, FULL
}