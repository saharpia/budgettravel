package com.tripbudget.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbudget.app.data.CategoryTotal
import com.tripbudget.app.data.Expense
import com.tripbudget.app.data.ExpenseRepository
import com.tripbudget.app.data.Trip
import com.tripbudget.app.data.TripRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DashboardUiState(
    val trip: Trip? = null,
    val expenses: List<Expense> = emptyList(),
    val spentMinorUnits: Long = 0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val dayOfTrip: Int = 1,
    val totalDays: Int = 1,
    val loading: Boolean = true,
) {
    val remainingMinorUnits: Long get() = (trip?.budgetMinorUnits ?: 0) - spentMinorUnits

    /** "€9/day under pace" — the pace framing from the design, not just a
     * percentage-used stat. Positive = under pace (good). */
    val paceMinorUnitsPerDay: Long get() {
        val trip = trip ?: return 0
        if (dayOfTrip <= 0) return 0
        val idealSpentSoFar = trip.budgetMinorUnits * dayOfTrip / totalDays.coerceAtLeast(1)
        val diff = idealSpentSoFar - spentMinorUnits
        return diff / dayOfTrip.coerceAtLeast(1)
    }
}

class DashboardViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        tripRepository.observeActiveTrip()
            .flatMapLatest { trip ->
                if (trip == null) {
                    flowOf(DashboardUiState(loading = false))
                } else {
                    combine(
                        expenseRepository.observeForTrip(trip.id),
                        expenseRepository.observeTotalSpent(trip.id),
                        expenseRepository.observeCategoryTotals(trip.id),
                    ) { expenses, spent, categories ->
                        val today = LocalDate.now().toEpochDay()
                        val dayOfTrip = (today - trip.startDateEpochDay + 1).toInt().coerceAtLeast(1)
                        val totalDays = (trip.endDateEpochDay - trip.startDateEpochDay + 1).toInt().coerceAtLeast(1)
                        DashboardUiState(
                            trip = trip,
                            expenses = expenses,
                            spentMinorUnits = spent,
                            categoryTotals = categories,
                            dayOfTrip = dayOfTrip.coerceAtMost(totalDays),
                            totalDays = totalDays,
                            loading = false,
                        )
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
