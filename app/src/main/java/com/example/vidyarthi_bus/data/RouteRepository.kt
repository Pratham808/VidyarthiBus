package com.example.vidyarthi_bus.data

import android.util.Log
import com.example.vidyarthi_bus.data.local.RouteDao
import com.example.vidyarthi_bus.domain.model.Route
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val routeDao: RouteDao
) {
    private val database = FirebaseDatabase.getInstance().getReference("routes")

    fun getRoutes(): Flow<List<Route>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RouteRepository", "Firebase Cancelled: ${error.message}")
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }.map { snapshot ->
        withContext(Dispatchers.IO) {
            try {
                snapshot.children.mapNotNull { child ->
                    try {
                        child.getValue(Route::class.java)
                    } catch (e: Exception) {
                        Log.e("RouteRepository", "Mapping error for child ${child.key}", e)
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("RouteRepository", "Snapshot mapping error", e)
                emptyList<Route>()
            }
        }
    }.onEach { routes ->
        try {
            routeDao.clearAll()
            routeDao.insertRoutes(routes.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e("RouteRepository", "Room sync error", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getLocalRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutes().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    fun getRouteById(routeId: String): Flow<Route?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child(routeId).addValueEventListener(listener)
        awaitClose { database.child(routeId).removeEventListener(listener) }
    }.map { snapshot ->
        withContext(Dispatchers.IO) {
            try {
                snapshot.getValue(Route::class.java)
            } catch (e: Exception) {
                Log.e("RouteRepository", "Single route mapping error", e)
                null
            }
        }
    }.flowOn(Dispatchers.IO)
}