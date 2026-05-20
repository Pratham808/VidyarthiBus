package com.example.vidyarthi_bus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.vidyarthi_bus.ui.screens.SplashScreen
import com.example.vidyarthi_bus.ui.screens.OnboardingScreen
import com.example.vidyarthi_bus.ui.screens.LoginScreen
import com.example.vidyarthi_bus.ui.screens.HomeScreen
import com.example.vidyarthi_bus.ui.screens.CrowdMeterScreen
import com.example.vidyarthi_bus.ui.screens.SharedAutoScreen
import com.example.vidyarthi_bus.ui.screens.ChatbotScreen
import com.example.vidyarthi_bus.ui.screens.AdminPanelScreen
import com.example.vidyarthi_bus.ui.screens.AnalyticsScreen
import com.example.vidyarthi_bus.ui.screens.SettingsScreen
import com.example.vidyarthi_bus.ui.screens.RouteSelectionScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.RouteSelection.route) {
            RouteSelectionScreen(navController)
        }
        composable(
            route = Screen.CrowdMeter.route,
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            CrowdMeterScreen(navController, routeId)
        }
        composable(Screen.SharedAuto.route) {
            SharedAutoScreen(navController)
        }
        composable(Screen.AdminPanel.route) {
            AdminPanelScreen(navController)
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.Chatbot.route) {
            ChatbotScreen(navController)
        }
    }
}