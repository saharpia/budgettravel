package com.tripbudget.app.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbudget.app.data.CategoryTotal
import com.tripbudget.app.data.ExpenseRepository
import com.tripbudget.app.data.Trip
import com.tripbudget.app.data.TripRepository
import kotlinx.coroutines.flow.*

data class TripReportUiState(
    val trip: Trip? = null,
    val spentMinorUnits: Long = 0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
) {
    val remainingMinorUnits: Long get() = (trip?.budgetMinorUnits ?: 0) - spentMinorUnits
    val spentFraction: Float get() {
        val budget = trip?.budgetMinorUnits ?: return 0f
        return if (budget <= 0) 0f else (spentMinorUnits.toFloat() / budget.toFloat()).coerceIn(0f, 1f)
    }
}

class TripReportViewModel(
    tripRepository: TripRepository,
    expenseRepository: ExpenseRepository,
) : ViewModel() {

    val uiState: StateFlow<TripReportUiState> =
        tripRepository.observeActiveTrip()
            .flatMapLatest { trip ->
                if (trip == null) {
                    flowOf(TripReportUiState())
                } else {
                    combine(
                        expenseRepository.observeTotalSpent(trip.id),
                        expenseRepository.observeCategoryTotals(trip.id),
                    ) { spent, categories ->
                        TripReportUiState(trip = trip, spentMinorUnits = spent, categoryTotals = categories)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripReportUiState())

    /** Renders the shareable card region as a PNG the OS share sheet can send
     * elsewhere. Left unimplemented in this scaffold — see the TODO in
     * TripReportScreen for the Compose `graphicsLayer`/`ImageBitmap` capture
     * approach to wire up. */
    fun shareReportImage() {
        // TODO: capture the card Composable to a Bitmap and launch an
        // ACTION_SEND intent with a content:// URI from a FileProvider.
    }
}
