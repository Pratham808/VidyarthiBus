package com.example.vidyarthi_bus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vidyarthi_bus.R
import com.example.vidyarthi_bus.ui.navigation.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = listOf(
        OnboardingPage("Live Crowd Tracking", "Know if the bus is full before it arrives."),
        OnboardingPage("Smart Predictions", "AI-powered occupancy forecasts for your route."),
        OnboardingPage("Shared Autos", "Find alternative transport when buses are full.")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = pages[currentPage].title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = pages[currentPage].description,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (currentPage < pages.size - 1) stringResource(R.string.next) else stringResource(R.string.get_started))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }) {
                Text("Skip")
            }
        }
    }
}

data class OnboardingPage(val title: String, val description: String)