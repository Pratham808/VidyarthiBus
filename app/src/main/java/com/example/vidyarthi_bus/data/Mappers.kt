package com.example.vidyarthi_bus.data

import com.example.vidyarthi_bus.data.local.RouteEntity
import com.example.vidyarthi_bus.domain.model.Route

fun RouteEntity.toDomain(): Route {
    return Route(
        id = id,
        name = name,
        from = from,
        to = to,
        departureTime = departureTime,
        village = village,
        isActive = isActive,
        lastCrowdLevel = lastCrowdLevel,
        lastReportTime = lastReportTime
    )
}

fun Route.toEntity(): RouteEntity {
    return RouteEntity(
        id = id,
        name = name,
        from = from,
        to = to,
        departureTime = departureTime,
        village = village,
        isActive = isActive,
        lastCrowdLevel = lastCrowdLevel,
        lastReportTime = lastReportTime
    )
}