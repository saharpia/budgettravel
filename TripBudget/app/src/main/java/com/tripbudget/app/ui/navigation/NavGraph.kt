package com.tripbudget.app.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Methods : Screen("methods")
    data object TripSetup : Screen("trip_setup")
    data object Dashboard : Screen("dashboard")
    data object Insights : Screen("insights")
    data object TripReport : Screen("trip_report")
    data object ReceiptCapture : Screen("receipt_capture")
}
