package com.example.vidyarthi_bus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vidyarthi_bus.R
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.ui.theme.CrowdGreen
import com.example.vidyarthi_bus.ui.theme.CrowdRed
import com.example.vidyarthi_bus.ui.theme.CrowdYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdMeterScreen(navController: NavController, routeId: String, viewModel: CrowdMeterViewModel = hiltViewModel()) {
    val route by viewModel.route.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(routeId) {
        viewModel.loadRoute(routeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(route?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Report Current Crowd",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            CrowdReportButton(
                text = stringResource(R.string.report_empty),
                color = CrowdGreen,
                onClick = { 
                    viewModel.reportCrowd(routeId, CrowdLevel.EMPTY) {
                        Toast.makeText(context, "Please wait before reporting again", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CrowdReportButton(
                text = stringResource(R.string.report_half_full),
                color = CrowdYellow,
                onClick = { 
                    viewModel.reportCrowd(routeId, CrowdLevel.HALF_FULL) {
                        Toast.makeText(context, "Please wait before reporting again", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CrowdReportButton(
                text = stringResource(R.string.report_full),
                color = CrowdRed,
                onClick = { 
                    viewModel.reportCrowd(routeId, CrowdLevel.FULL) {
                        Toast.makeText(context, "Please wait before reporting again", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            if (route?.lastCrowdLevel == CrowdLevel.FULL) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bus is FULL!", fontWeight = FontWeight.Bold)
                        Text("AI recommends taking a shared auto.")
                        TextButton(onClick = { navController.navigate("shared_auto") }) {
                            Text("View Shared Autos")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrowdReportButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}