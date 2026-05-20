package com.example.vidyarthi_bus.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthi_bus.data.RouteRepository
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.domain.model.Route
import com.example.vidyarthi_bus.domain.model.Report
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrowdMeterViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {

    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    private var lastReportTime = 0L

    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            repository.getRouteById(routeId).collect {
                _route.value = it
            }
        }
    }

    fun reportCrowd(routeId: String, level: CrowdLevel, onSpam: () -> Unit = {}) {
        val currentTime = System.currentTimeMillis()
        // Anti-Spam: Prevent reporting more than once every 30 seconds
        if (currentTime - lastReportTime < 30000) {
            onSpam()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: "guest"
        
        val reportId = database.getReference("reports").push().key ?: return
        val report = Report(id = reportId, routeId = routeId, userId = userId, crowdLevel = level)
        
        database.getReference("reports").child(reportId).setValue(report)
        
        // Update route's last crowd level with optimistic UI sync
        database.getReference("routes").child(routeId).updateChildren(
            mapOf(
                "lastCrowdLevel" to level.name,
                "lastReportTime" to currentTime
            )
        )
        
        lastReportTime = currentTime
    }
}