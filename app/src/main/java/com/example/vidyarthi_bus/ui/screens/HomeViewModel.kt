package com.example.vidyarthi_bus.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthi_bus.data.RouteRepository
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.domain.model.Route
import com.example.vidyarthi_bus.domain.model.Announcement
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private val _aiPrediction = MutableStateFlow("Analyzing crowd patterns...")
    val aiPrediction: StateFlow<String> = _aiPrediction.asStateFlow()

    private val _confidence = MutableStateFlow(0)
    val confidence: StateFlow<Int> = _confidence.asStateFlow()

    private val _latestAlert = MutableStateFlow<Announcement?>(null)
    val latestAlert: StateFlow<Announcement?> = _latestAlert.asStateFlow()

    private val _isSyncing = MutableStateFlow(true)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        loadData()
        listenForAlerts()
    }

    private fun loadData() {
        // Step 1: Load from Local Cache immediately
        viewModelScope.launch {
            repository.getLocalRoutes().collect { localList ->
                if (localList.isNotEmpty()) {
                    _routes.value = localList
                    _isSyncing.value = false // We have data to show, can stop showing full-screen loader
                    generateSmartRecommendations(localList)
                }
            }
        }

        // Step 2: Sync with Firebase in background
        viewModelScope.launch {
            repository.getRoutes().collect { remoteList ->
                _routes.value = remoteList
                _isSyncing.value = false
                generateSmartRecommendations(remoteList)
            }
        }
    }

    private fun generateSmartRecommendations(routes: List<Route>) {
        if (routes.isEmpty()) return
        val fullRoutes = routes.filter { it.lastCrowdLevel == CrowdLevel.FULL }
        val clearRoutes = routes.filter { it.lastCrowdLevel == CrowdLevel.EMPTY }

        when {
            fullRoutes.size > routes.size / 2 -> {
                _aiPrediction.value = "High crowd detected. Shared auto recommended."
                _confidence.value = 94
            }
            clearRoutes.isNotEmpty() -> {
                val bestRoute = clearRoutes.first()
                _aiPrediction.value = "Smart Choice: ${bestRoute.name} is currently EMPTY."
                _confidence.value = 88
            }
            else -> {
                _aiPrediction.value = "Moderate crowd. Board early for a seat."
                _confidence.value = 75
            }
        }
    }

    private fun listenForAlerts() {
        val db = FirebaseDatabase.getInstance().getReference("announcements")
        db.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alert = snapshot.children.firstOrNull()?.getValue(Announcement::class.java)
                _latestAlert.value = alert
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}