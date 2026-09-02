package com.tripbudget.app.ui.quickadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbudget.app.data.Expense
import com.tripbudget.app.data.ExpenseRepository
import com.tripbudget.app.data.ExpenseSource
import com.tripbudget.app.data.TripRepository
import com.tripbudget.app.parser.ExpenseParser
import com.tripbudget.app.parser.ParsedExpense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class QuickAddUiState(
    val inputText: String = "",
    val parsed: ParsedExpense? = null,
    val saved: Boolean = false,
)

class QuickAddViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAddUiState())
    val uiState: StateFlow<QuickAddUiState> = _uiState.asStateFlow()

    private var activeTripCurrency = "USD"
    private var activeTripId: Long? = null

    init {
        viewModelScope.launch {
            val trip = tripRepository.observeActiveTrip().first()
            if (trip != null) {
                activeTripCurrency = trip.currencyCode
                activeTripId = trip.id
            }
        }
    }

    fun onTextChanged(text: String) {
        val parsed = ExpenseParser.parse(text, activeTripCurrency)
        _uiState.value = _uiState.value.copy(inputText = text, parsed = parsed)
    }

    fun save() {
        val tripId = activeTripId ?: return
        val parsed = _uiState.value.parsed ?: return
        viewModelScope.launch {
            expenseRepository.add(
                Expense(
                    tripId = tripId,
                    description = parsed.description,
                    amountMinorUnits = parsed.amountMinorUnits,
                    currencyCode = parsed.currencyCode,
                    category = parsed.category,
                    source = ExpenseSource.TYPED,
                    occurredAtEpochMillis = System.currentTimeMillis(),
                    createdAtEpochMillis = System.currentTimeMillis(),
                ),
            )
            _uiState.value = QuickAddUiState(saved = true)
        }
    }

    fun reset() {
        _uiState.value = QuickAddUiState()
    }
}
