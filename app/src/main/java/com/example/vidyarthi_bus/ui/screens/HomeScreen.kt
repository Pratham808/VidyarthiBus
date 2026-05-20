package com.example.vidyarthi_bus.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vidyarthi_bus.R
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.domain.model.Route
import com.example.vidyarthi_bus.ui.components.LivePulseIndicator
import com.example.vidyarthi_bus.ui.navigation.Screen
import com.example.vidyarthi_bus.ui.theme.CrowdGreen
import com.example.vidyarthi_bus.ui.theme.CrowdRed
import com.example.vidyarthi_bus.ui.theme.CrowdYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val routes by viewModel.routes.collectAsState()
    val aiPrediction by viewModel.aiPrediction.collectAsState()
    val confidence by viewModel.confidence.collectAsState()
    val latestAlert by viewModel.latestAlert.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.CALL_PHONE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Analytics.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isSyncing && routes.isEmpty()) {
                // Full Screen Loader only if we have NO data at all
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Initializing Intelligent Network...", color = MaterialTheme.colorScheme.primary)
                }
            } else if (routes.isEmpty()) {
                // Empty State if sync finished but no routes exist
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🚌", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Active Routes Found", style = MaterialTheme.typography.titleLarge)
                    Text("Check back later or add routes via Admin Panel.", textAlign = TextAlign.Center, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate(Screen.AdminPanel.route) }) {
                        Text("Open Admin Panel")
                    }
                }
            } else {
                // Main Dashboard
                Column(modifier = Modifier.fillMaxSize()) {
                    if (isSyncing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    
                    latestAlert?.let { alert ->
                        Card(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(alert.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    Text(alert.message, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Card(
                        onClick = { navController.navigate(Screen.Chatbot.route) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(stringResource(R.string.welcome_message), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("AI Predicts: $aiPrediction", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Confidence: $confidence%", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.nearby_routes), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { navController.navigate(Screen.Analytics.route) }) { Text("View Trends") }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(routes) { route ->
                            RouteCard(route = route) {
                                navController.navigate(Screen.CrowdMeter.createRoute(route.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(route: Route, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(route.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${route.from} ➔ ${route.to}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            LivePulseIndicator(color = getCrowdColor(route.lastCrowdLevel))
        }
    }
}

fun getCrowdColor(level: CrowdLevel): Color {
    return when (level) {
        CrowdLevel.EMPTY -> CrowdGreen
        CrowdLevel.HALF_FULL -> CrowdYellow
        CrowdLevel.FULL -> CrowdRed
    }
}