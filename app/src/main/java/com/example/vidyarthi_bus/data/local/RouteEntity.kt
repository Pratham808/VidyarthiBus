package com.example.vidyarthi_bus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vidyarthi_bus.domain.model.CrowdLevel

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val from: String,
    val to: String,
    val departureTime: String,
    val village: String,
    val isActive: Boolean,
    val lastCrowdLevel: CrowdLevel,
    val lastReportTime: Long
)