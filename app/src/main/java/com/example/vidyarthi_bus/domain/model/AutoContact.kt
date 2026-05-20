package com.example.vidyarthi_bus.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AutoContact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val village: String = "",
    val isAvailable: Boolean = true
)