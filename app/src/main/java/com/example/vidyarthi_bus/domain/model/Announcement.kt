package com.example.vidyarthi_bus.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: AnnouncementType = AnnouncementType.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnnouncementType {
    INFO, WARNING, EMERGENCY
}