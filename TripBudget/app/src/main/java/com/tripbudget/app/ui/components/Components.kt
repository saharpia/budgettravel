package com.tripbudget.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tripbudget.app.data.Category
import com.tripbudget.app.data.Expense
import com.tripbudget.app.ui.theme.*
import com.tripbudget.app.util.formatMoney

/** The budget-and-progress block at the top of the dashboard, per the spec:
 * budget and amount used, front and center. */
@Composable
fun BudgetHeader(
    tripName: String,
    dayLabel: String,
    remainingMinorUnits: Long,
    spentMinorUnits: Long,
    budgetMinorUnits: Long,
    currencyCode: String,
    paceLabel: String,
    modifier: Modifier = Modifier,
) {
    val fraction = if (budgetMinorUnits <= 0) 0f else
        (spentMinorUnits.toFloat() / budgetMinorUnits.toFloat()).coerceIn(0f, 1f)
    val animatedFraction = remember { Animatable(0f) }
    LaunchedEffect(fraction) {
        animatedFraction.animateTo(fraction, animationSpec = tween(900))
    }

    Column(
        modifier = modifier
            .background(Teal, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .padding(horizontal = 22.dp, vertical = 26.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(tripName.uppercase(), style = MaterialTheme.typography.labelLarge, color = TealMuted)
            Surface(shape = RoundedCornerShape(50), color = Mint) {
                Text(
                    dayLabel,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MintText,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("STILL YOURS", style = MaterialTheme.typography.labelLarge, color = TealMuted)
        Text(
            formatMoney(remainingMinorUnits, currencyCode),
            style = MaterialTheme.typography.displayLarge,
            color = Cream,
        )

        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(animatedFraction.value)
                    .fillMaxHeight()
                    .background(Mint, RoundedCornerShape(50)),
            )
        }
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "${formatMoney(spentMinorUnits, currencyCode)} of ${formatMoney(budgetMinorUnits, currencyCode)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TealMuted,
            )
            Text(paceLabel, style = MaterialTheme.typography.bodyMedium, color = Mint)
        }
    }
}

/** The single, actionable insight card — the app's real differentiator, so
 * it always sits directly under the budget header, never buried in a tab. */
@Composable
fun InsightCard(headline: String, body: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MintLight,
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = TealDark, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("SPOTTED", style = MaterialTheme.typography.labelLarge, color = TealDark)
            }
            Spacer(Modifier.height(9.dp))
            Text(headline, style = MaterialTheme.typography.titleLarge, color = MintText)
            Spacer(Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MintMuted)
        }
    }
}

@Composable
fun ExpenseRow(expense: Expense, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(), color = CardWhite, shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                expense.description,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                formatMoney(expense.amountMinorUnits, expense.currencyCode, showDecimals = true),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

fun categoryColor(category: Category): Color = when (category) {
    Category.FOOD -> CategoryFood
    Category.STAYS -> CategoryStays
    Category.TRANSPORT -> CategoryTransport
    Category.OTHER -> CategoryOther
}

@Composable
fun CategoryChip(label: String, amountMinorUnits: Long, currencyCode: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = CardWhite, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = SoftMuted)
            Spacer(Modifier.height(5.dp))
            Text(formatMoney(amountMinorUnits, currencyCode), style = MaterialTheme.typography.headlineMedium)
        }
    }
}
