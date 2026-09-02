package com.tripbudget.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import com.tripbudget.app.TripBudgetApp
import com.tripbudget.app.ui.dashboard.DashboardViewModel
import com.tripbudget.app.ui.insights.InsightsViewModel
import com.tripbudget.app.ui.quickadd.QuickAddViewModel
import com.tripbudget.app.ui.report.TripReportViewModel
import androidx.compose.ui.platform.LocalContext

/**
 * A tiny hand-rolled factory rather than pulling in a DI framework
 * (Hilt/Koin) — this is a scaffold, and four ViewModels with two
 * constructor dependencies each don't justify the extra setup yet. If the
 * app grows past this, Hilt is the natural next step.
 */
class AppViewModelFactory(private val app: TripBudgetApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        DashboardViewModel::class.java -> DashboardViewModel(app.tripRepository, app.expenseRepository) as T
        QuickAddViewModel::class.java -> QuickAddViewModel(app.tripRepository, app.expenseRepository) as T
        InsightsViewModel::class.java -> InsightsViewModel(app.tripRepository, app.expenseRepository) as T
        TripReportViewModel::class.java -> TripReportViewModel(app.tripRepository, app.expenseRepository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

@Composable
fun appViewModelFactory(): AppViewModelFactory {
    val app = LocalContext.current.applicationContext as TripBudgetApp
    return AppViewModelFactory(app)
}
