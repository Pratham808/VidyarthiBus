package com.example.vidyarthi_bus.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object RouteSelection : Screen("route_selection")
    object CrowdMeter : Screen("crowd_meter/{routeId}") {
        fun createRoute(routeId: String) = "crowd_meter/$routeId"
    }
    object SharedAuto : Screen("shared_auto")
    object AdminPanel : Screen("admin_panel")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object Chatbot : Screen("chatbot")
}