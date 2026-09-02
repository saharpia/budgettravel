package com.tripbudget.app.util

import java.util.Currency
import java.util.Locale

/** Formats minor units (cents) as "€412", using the currency's own symbol. */
fun formatMoney(minorUnits: Long, currencyCode: String, showDecimals: Boolean = false): String {
    val symbol = runCatching { Currency.getInstance(currencyCode).getSymbol(Locale.getDefault()) }
        .getOrDefault(currencyCode)
    val whole = minorUnits / 100
    val cents = kotlin.math.abs(minorUnits % 100)
    return if (showDecimals) {
        String.format(Locale.getDefault(), "%s%d.%02d", symbol, whole, cents)
    } else {
        "$symbol$whole"
    }
}
