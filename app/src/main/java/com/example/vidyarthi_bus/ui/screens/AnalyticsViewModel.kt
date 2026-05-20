package com.example.vidyarthi_bus.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthi_bus.data.RouteRepository
import com.example.vidyarthi_bus.domain.model.Route
import com.example.vidyarthi_bus.domain.model.Report
import com.example.vidyarthi_bus.utils.PredictionEngine
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {

    private val _routeStats = MutableStateFlow<List<Route>>(emptyList())
    val routeStats: StateFlow<List<Route>> = _routeStats.asStateFlow()

    private val _routeReliability = MutableStateFlow<Map<String, Int>>(emptyMap())
    val routeReliability: StateFlow<Map<String, Int>> = _routeReliability.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getRoutes().collect { routes ->
                _routeStats.value = routes
                calculateRealReliability(routes)
            }
        }
    }

    private fun calculateRealReliability(routes: List<Route>) {
        val db = FirebaseDatabase.getInstance().getReference("reports")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allReports = snapshot.children.mapNotNull { it.getValue(Report::class.java) }
                val reliabilityMap = routes.associate { route ->
                    val routeReports = allReports.filter { it.routeId == route.id }
                    route.id to PredictionEngine.getConfidenceScore(routeReports)
                }
                _routeReliability.value = reliabilityMap
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}