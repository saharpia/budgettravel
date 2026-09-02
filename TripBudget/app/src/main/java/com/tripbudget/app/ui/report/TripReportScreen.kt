package com.tripbudget.app.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbudget.app.ui.appViewModelFactory
import com.tripbudget.app.ui.components.categoryColor
import com.tripbudget.app.ui.theme.*
import com.tripbudget.app.util.formatMoney

@Composable
fun TripReportScreen() {
    val viewModel: TripReportViewModel = viewModel(factory = appViewModelFactory())
    val state by viewModel.uiState.collectAsState()
    val trip = state.trip ?: run {
        Box(Modifier.fillMaxSize().background(Cream)) { }
        return
    }

    Column(Modifier.fillMaxSize().background(Cream).padding(20.dp)) {
        Text("Trip report", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // The shareable card. TODO: capture this Column via
        // androidx.compose.ui.graphics.layer / drawToBitmap and hand the
        // resulting PNG to an ACTION_SEND share intent from
        // TripReportViewModel.shareReportImage().
        Surface(color = Teal, shape = RoundedCornerShape(26.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp)) {
                Text(trip.name, style = MaterialTheme.typography.headlineMedium, color = Cream)
                Spacer(Modifier.height(18.dp))
                Text(
                    formatMoney(state.spentMinorUnits, trip.currencyCode),
                    style = MaterialTheme.typography.displayLarge,
                    color = Cream,
                )
                Text(
                    "of ${formatMoney(trip.budgetMinorUnits, trip.currencyCode)} spent",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TealMuted,
                )

                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth().height(12.dp).background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))) {
                    Box(Modifier.fillMaxWidth(state.spentFraction).height(12.dp).background(Mint, RoundedCornerShape(50)))
                }

                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(
                        value = formatMoney(state.remainingMinorUnits.coerceAtLeast(0), trip.currencyCode),
                        label = if (state.remainingMinorUnits >= 0) "under budget" else "over budget",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("WHERE IT WENT", style = MaterialTheme.typography.labelLarge, color = SoftMuted)
        Spacer(Modifier.height(10.dp))
        Surface(color = CardWhite, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                val total = state.categoryTotals.sumOf { it.totalMinorUnits }.coerceAtLeast(1)
                state.categoryTotals.forEach { ct ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Text(ct.category.displayName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(formatMoney(ct.totalMinorUnits, trip.currencyCode), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = viewModel::shareReportImage,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Cream),
            shape = RoundedCornerShape(50),
        ) {
            Icon(Icons.Filled.Share, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Share trip report", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.10f), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(13.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = Cream)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TealMuted)
        }
    }
}
