package com.tripbudget.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbudget.app.ui.appViewModelFactory
import com.tripbudget.app.ui.components.BudgetHeader
import com.tripbudget.app.ui.components.CategoryChip
import com.tripbudget.app.ui.components.ExpenseRow
import com.tripbudget.app.ui.components.InsightCard
import com.tripbudget.app.ui.components.categoryColor
import com.tripbudget.app.ui.theme.Cream
import com.tripbudget.app.ui.theme.SoftMuted
import com.tripbudget.app.ui.theme.Teal
import com.tripbudget.app.util.formatMoney

@Composable
fun DashboardScreen(
    onQuickAdd: () -> Unit,
    onOpenReceiptCapture: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenReport: () -> Unit,
) {
    val viewModel: DashboardViewModel = viewModel(factory = appViewModelFactory())
    val state by viewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize().background(Cream)) {
        if (state.trip == null && !state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active trip yet.", color = SoftMuted)
            }
            return@Box
        }

        LazyColumn(Modifier.fillMaxSize().padding(bottom = 148.dp)) {
            item {
                state.trip?.let { trip ->
                    val pace = state.paceMinorUnitsPerDay
                    val paceLabel = if (pace >= 0) {
                        "${formatMoney(pace, trip.currencyCode)}/day under pace"
                    } else {
                        "${formatMoney(-pace, trip.currencyCode)}/day over pace"
                    }
                    BudgetHeader(
                        tripName = trip.name,
                        dayLabel = "Day ${state.dayOfTrip} / ${state.totalDays}",
                        remainingMinorUnits = state.remainingMinorUnits,
                        spentMinorUnits = state.spentMinorUnits,
                        budgetMinorUnits = trip.budgetMinorUnits,
                        currencyCode = trip.currencyCode,
                        paceLabel = paceLabel,
                    )
                }
            }

            item {
                // TODO: this headline is a placeholder — wire it up once
                // there's more than one trip to compare category share
                // against (see InsightsViewModel's comment on the same gap).
                InsightCard(
                    headline = "Watch your food & drink spend.",
                    body = "It's your largest category so far this trip.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                )
            }

            if (state.categoryTotals.isNotEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        state.categoryTotals.forEach { ct ->
                            state.trip?.let { trip ->
                                CategoryChip(
                                    label = ct.category.displayName,
                                    amountMinorUnits = ct.totalMinorUnits,
                                    currencyCode = trip.currencyCode,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            item {
                Text(
                    "TODAY",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoftMuted,
                    modifier = Modifier.padding(start = 22.dp, top = 22.dp, bottom = 10.dp),
                )
            }

            if (state.expenses.isEmpty() && !state.loading) {
                item {
                    Text(
                        "No expenses yet — try the quick-add button below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMuted,
                        modifier = Modifier.padding(horizontal = 22.dp),
                    )
                }
            }

            items(state.expenses, key = { it.id }) { expense ->
                ExpenseRow(expense, modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp))
            }
        }

        // Persistent quick-add bar + camera shortcut, and bottom nav.
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = onQuickAdd,
                    modifier = Modifier.weight(1f),
                    color = androidx.compose.ui.graphics.Color.White,
                    shape = RoundedCornerShape(50),
                    shadowElevation = 6.dp,
                ) {
                    Row(
                        Modifier.padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "coffee — 5 euro",
                            color = SoftMuted,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = onOpenReceiptCapture) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Photograph a receipt")
                        }
                        FilledIconButton(onClick = onQuickAdd, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Teal)) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Add expense", tint = Cream)
                        }
                    }
                }
            }

            NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                NavigationBarItem(selected = true, onClick = {}, icon = {}, label = { Text("Trip") })
                NavigationBarItem(selected = false, onClick = onOpenInsights, icon = {}, label = { Text("Insights") })
                NavigationBarItem(selected = false, onClick = onOpenReport, icon = {}, label = { Text("Report") })
            }
        }
    }
}
