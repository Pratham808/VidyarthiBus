package com.example.vidyarthi_bus.utils

import androidx.room.TypeConverter
import com.example.vidyarthi_bus.domain.model.CrowdLevel

class Converters {
    @TypeConverter
    fun fromCrowdLevel(value: CrowdLevel): String {
        return value.name
    }

    @TypeConverter
    fun toCrowdLevel(value: String): CrowdLevel {
        return CrowdLevel.valueOf(value)
    }
}