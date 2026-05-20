package com.example.vidyarthi_bus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vidyarthi_bus.domain.model.CrowdLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val stats by viewModel.routeStats.collectAsState()
    val reliabilityMap by viewModel.routeReliability.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Busiest Routes", style = MaterialTheme.typography.titleMedium)
            }
            
            items(stats) { route ->
                val occupancyScore = when(route.lastCrowdLevel) {
                    CrowdLevel.EMPTY -> 20
                    CrowdLevel.HALF_FULL -> 60
                    CrowdLevel.FULL -> 100
                }
                val reliability = reliabilityMap[route.id] ?: 0
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(route.name, fontWeight = FontWeight.Bold)
                            Text("Occupancy: $occupancyScore%", fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(occupancyScore / 100f)
                                    .fillMaxHeight()
                                    .background(
                                        color = if (occupancyScore > 80) Color.Red else if (occupancyScore > 40) Color.Yellow else Color.Green,
                                        shape = MaterialTheme.shapes.small
                                    )
                            )
                        }
                        
                        Text("AI Prediction Confidence: $reliability%", modifier = Modifier.padding(top = 8.dp), fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Peak Crowd Timings", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Morning Rush: 8:15 AM - 8:45 AM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text("Probability of FULL bus: 94%", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}