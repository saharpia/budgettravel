package com.tripbudget.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripbudget.app.data.Trip
import com.tripbudget.app.ui.theme.*
import java.time.LocalDate

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            "Know where your\ntrip money goes.",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
            color = Cream,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Set a budget, log spending in seconds, and get a report worth sharing when the trip's done.",
            style = MaterialTheme.typography.bodyLarge,
            color = TealMuted,
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = MintText),
            shape = RoundedCornerShape(50),
        ) {
            Text("Get started", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun MethodsScreen(onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Cream).padding(24.dp)) {
        Spacer(Modifier.height(32.dp))
        Text("Log an expense\nthree ways.", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(10.dp))
        Text(
            "Whichever is fastest in the moment. All of it works offline.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedInk,
        )
        Spacer(Modifier.height(24.dp))

        MethodRow(Icons.Filled.Edit, "Just type it", "\"coffee — 5 euro\" becomes a logged, categorized expense in one line.")
        Spacer(Modifier.height(14.dp))
        MethodRow(Icons.Filled.PhotoCamera, "Snap the receipt", "Photograph it — we read the total, merchant, and date for you.")
        Spacer(Modifier.height(14.dp))
        MethodRow(Icons.Filled.Description, "Import a PDF", "Booking confirmations and e-receipts — drop the file, we pull out the cost.")

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Cream),
            shape = RoundedCornerShape(50),
        ) {
            Text("Continue", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MethodRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    Surface(color = CardWhite, shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(46.dp).background(MintLight, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = TealDark)
            }
            Spacer(Modifier.width(15.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(3.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium, color = MutedInk)
            }
        }
    }
}

@Composable
fun TripSetupScreen(onStartTracking: (Trip) -> Unit) {
    var tripName by remember { mutableStateOf("My trip") }
    var budgetEuros by remember { mutableStateOf(1100) }
    var tripDays by remember { mutableStateOf(12) }
    var currency by remember { mutableStateOf("EUR") }

    Column(Modifier.fillMaxSize().background(Cream).padding(24.dp)) {
        Spacer(Modifier.height(32.dp))
        Text("Set up this trip.", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(10.dp))
        Text("Takes ten seconds. You can change any of this later.", style = MaterialTheme.typography.bodyLarge, color = MutedInk)

        Spacer(Modifier.height(24.dp))
        Text("TRIP NAME", style = MaterialTheme.typography.labelLarge, color = SoftMuted)
        OutlinedTextField(
            value = tripName,
            onValueChange = { tripName = it },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(Modifier.height(18.dp))
        Text("TOTAL BUDGET", style = MaterialTheme.typography.labelLarge, color = SoftMuted)
        Surface(color = CardWhite, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("€$budgetEuros", style = MaterialTheme.typography.displayLarge.copy(fontSize = 30.sp), color = Teal)
                Slider(
                    value = budgetEuros.toFloat(),
                    onValueChange = { budgetEuros = it.toInt() },
                    valueRange = 100f..5000f,
                    colors = SliderDefaults.colors(thumbColor = Teal, activeTrackColor = Teal),
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("CURRENCY", style = MaterialTheme.typography.labelLarge, color = SoftMuted)
        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("EUR" to "€", "USD" to "$", "GBP" to "£").forEach { (code, symbol) ->
                val selected = currency == code
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (selected) Teal else CardWhite,
                    shape = RoundedCornerShape(14.dp),
                    onClick = { currency = code },
                ) {
                    Text(
                        "$code $symbol",
                        modifier = Modifier.padding(13.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (selected) Cream else SoftMuted,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                val today = LocalDate.now()
                onStartTracking(
                    Trip(
                        name = tripName.ifBlank { "My trip" },
                        startDateEpochDay = today.toEpochDay(),
                        endDateEpochDay = today.plusDays((tripDays - 1).toLong()).toEpochDay(),
                        budgetMinorUnits = budgetEuros * 100L,
                        currencyCode = currency,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Cream),
            shape = RoundedCornerShape(50),
        ) {
            Text("Start tracking", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
    }
}
