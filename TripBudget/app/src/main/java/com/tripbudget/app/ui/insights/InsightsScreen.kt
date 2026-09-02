package com.tripbudget.app.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbudget.app.ui.appViewModelFactory
import com.tripbudget.app.ui.components.categoryColor
import com.tripbudget.app.ui.theme.*
import com.tripbudget.app.util.formatMoney

@Composable
fun InsightsScreen() {
    val viewModel: InsightsViewModel = viewModel(factory = appViewModelFactory())
    val state by viewModel.uiState.collectAsState()
    val trip = state.trip

    Column(Modifier.fillMaxSize().background(Cream).padding(20.dp)) {
        Text("Insights", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (state.topCategoryLabel.isNotBlank()) {
            Surface(color = Teal, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("SPOTTED", style = MaterialTheme.typography.labelLarge, color = Mint)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${state.topCategoryLabel} is ${state.topCategoryShare}% of your spending so far.",
                        style = MaterialTheme.typography.titleLarge,
                        color = Cream,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Text("BY CATEGORY", style = MaterialTheme.typography.labelLarge, color = SoftMuted)
        Spacer(Modifier.height(10.dp))

        Surface(color = CardWhite, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                val total = state.categoryTotals.sumOf { it.totalMinorUnits }.coerceAtLeast(1)
                state.categoryTotals.forEach { ct ->
                    val fraction = ct.totalMinorUnits.toFloat() / total.toFloat()
                    Column(Modifier.padding(vertical = 12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ct.category.displayName, style = MaterialTheme.typography.titleMedium)
                            trip?.let {
                                Text(formatMoney(ct.totalMinorUnits, it.currencyCode), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .background(Divider, RoundedCornerShape(50)),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(fraction)
                                    .height(7.dp)
                                    .background(categoryColor(ct.category), RoundedCornerShape(50)),
                            )
                        }
                    }
                }
                if (state.categoryTotals.isEmpty()) {
                    Text(
                        "Log a few expenses to see your breakdown.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMuted,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            }
        }
    }
}
