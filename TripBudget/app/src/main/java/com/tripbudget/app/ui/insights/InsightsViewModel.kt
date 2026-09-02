package com.tripbudget.app.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbudget.app.data.CategoryTotal
import com.tripbudget.app.data.ExpenseRepository
import com.tripbudget.app.data.Trip
import com.tripbudget.app.data.TripRepository
import kotlinx.coroutines.flow.*

data class InsightsUiState(
    val trip: Trip? = null,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val topCategoryShare: Int = 0,
    val topCategoryLabel: String = "",
)

/**
 * "Dinners eat 41% of your trip" style copy needs a comparison point
 * (a past trip's category share) to be a real insight rather than a
 * restated stat. This scaffold has no other trips to compare against yet,
 * so `insightHeadline` falls back to a share-of-spend framing — swap in a
 * real comparison once historical trips exist.
 */
class InsightsViewModel(
    tripRepository: TripRepository,
    expenseRepository: ExpenseRepository,
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> =
        tripRepository.observeActiveTrip()
            .flatMapLatest { trip ->
                if (trip == null) {
                    flowOf(InsightsUiState())
                } else {
                    expenseRepository.observeCategoryTotals(trip.id).map { totals ->
                        val total = totals.sumOf { it.totalMinorUnits }
                        val top = totals.maxByOrNull { it.totalMinorUnits }
                        val share = if (total > 0 && top != null) (top.totalMinorUnits * 100 / total).toInt() else 0
                        InsightsUiState(
                            trip = trip,
                            categoryTotals = totals,
                            topCategoryShare = share,
                            topCategoryLabel = top?.category?.displayName ?: "",
                        )
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())
}
