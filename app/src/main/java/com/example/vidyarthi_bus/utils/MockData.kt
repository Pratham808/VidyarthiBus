package com.example.vidyarthi_bus.utils

import android.util.Log
import com.example.vidyarthi_bus.data.local.RouteDao
import com.example.vidyarthi_bus.data.toEntity
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.domain.model.Route
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockData @Inject constructor(
    private val routeDao: RouteDao
) {
    suspend fun seedData() = withContext(Dispatchers.IO) {
        val routes = listOf(
            Route("1", "Route 101", "Village A", "College", "8:00 AM", "A", true, CrowdLevel.EMPTY),
            Route("2", "Route 102", "Village B", "College", "8:15 AM", "B", true, CrowdLevel.HALF_FULL),
            Route("3", "Route 103", "Village C", "College", "8:30 AM", "C", true, CrowdLevel.FULL)
        )

        // Step 1: Seed Local Room DB for instant display
        try {
            routeDao.insertRoutes(routes.map { it.toEntity() })
            Log.d("MockData", "Local database seeded successfully")
        } catch (e: Exception) {
            Log.e("MockData", "Local seed failed: ${e.message}")
        }

        // Step 2: Seed Firebase for cloud sync
        try {
            val database = FirebaseDatabase.getInstance().getReference("routes")
            routes.forEach {
                database.child(it.id).setValue(it)
            }
            Log.d("MockData", "Firebase seeded successfully")
        } catch (e: Exception) {
            Log.e("MockData", "Firebase seed failed (likely offline): ${e.message}")
        }
    }
}