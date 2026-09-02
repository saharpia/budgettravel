package com.tripbudget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tripbudget.app.ui.dashboard.DashboardScreen
import com.tripbudget.app.ui.insights.InsightsScreen
import com.tripbudget.app.ui.navigation.Screen
import com.tripbudget.app.ui.onboarding.MethodsScreen
import com.tripbudget.app.ui.onboarding.TripSetupScreen
import com.tripbudget.app.ui.onboarding.WelcomeScreen
import com.tripbudget.app.ui.quickadd.QuickAddSheet
import com.tripbudget.app.ui.receipt.ReceiptCaptureScreen
import com.tripbudget.app.ui.report.TripReportScreen
import com.tripbudget.app.ui.theme.TripBudgetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBudgetTheme {
                TripBudgetNavHost()
            }
        }
    }
}

@Composable
private fun TripBudgetNavHost() {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as TripBudgetApp
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Skip onboarding if a trip already exists (returning user).
    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val hasTrip = app.tripRepository.observeActiveTrip()
        scope.launch {
            hasTrip.collect { trip ->
                if (startDestination == null) {
                    startDestination = if (trip != null) Screen.Dashboard.route else Screen.Welcome.route
                }
            }
        }
    }

    val destination = startDestination ?: return

    var showQuickAdd by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = destination) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.Methods.route) })
        }
        composable(Screen.Methods.route) {
            MethodsScreen(onContinue = { navController.navigate(Screen.TripSetup.route) })
        }
        composable(Screen.TripSetup.route) {
            TripSetupScreen(onStartTracking = { trip ->
                scope.launch {
                    app.tripRepository.createTrip(trip)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onQuickAdd = { showQuickAdd = true },
                onOpenReceiptCapture = { navController.navigate(Screen.ReceiptCapture.route) },
                onOpenInsights = { navController.navigate(Screen.Insights.route) },
                onOpenReport = { navController.navigate(Screen.TripReport.route) },
            )
        }
        composable(Screen.Insights.route) { InsightsScreen() }
        composable(Screen.TripReport.route) { TripReportScreen() }
        composable(Screen.ReceiptCapture.route) {
            ReceiptCaptureScreen(
                onClose = { navController.popBackStack() },
                onCaptured = { navController.popBackStack() },
            )
        }
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onSaved = { showQuickAdd = false },
        )
    }
}
