package com.tripbudget.app.parser

import com.tripbudget.app.data.Category
import java.util.Locale

/**
 * Result of parsing free-text expense entry, e.g. "coffee - 5 euro".
 * `confident` is false when we had to guess at the amount or currency, so
 * the UI can show the parsed preview but not let the user save until they
 * glance at it — never silently save a wrong number.
 */
data class ParsedExpense(
    val description: String,
    val amountMinorUnits: Long,
    val currencyCode: String,
    val category: Category,
    val confident: Boolean,
)

/**
 * Turns a single line of typed text into a structured expense. Deliberately
 * simple regex + keyword matching rather than an on-device ML model or a
 * network call to an LLM — it has to work instantly, offline, on any
 * device, for a short, fairly formulaic sentence shape. If false positives
 * turn out to be common in testing, this is the seam to swap in an on-device
 * NL model (e.g. ML Kit Entity Extraction) without changing callers.
 */
object ExpenseParser {

    private val currencySymbols = mapOf(
        "€" to "EUR", "$" to "USD", "£" to "GBP", "¥" to "JPY", "₪" to "ILS",
    )

    private val currencyWords = mapOf(
        "eur" to "EUR", "euro" to "EUR", "euros" to "EUR",
        "usd" to "USD", "dollar" to "USD", "dollars" to "USD", "buck" to "USD", "bucks" to "USD",
        "gbp" to "GBP", "pound" to "GBP", "pounds" to "GBP", "quid" to "GBP",
        "jpy" to "JPY", "yen" to "JPY",
        "ils" to "ILS", "shekel" to "ILS", "shekels" to "ILS", "nis" to "ILS",
    )

    private val categoryKeywords: Map<Category, List<String>> = mapOf(
        Category.FOOD to listOf(
            "coffee", "tea", "lunch", "dinner", "breakfast", "brunch", "snack", "restaurant",
            "cafe", "café", "bar", "drinks", "beer", "wine", "food", "pizza", "burger", "tapas",
            "nata", "pastry", "groceries", "market",
        ),
        Category.STAYS to listOf(
            "hotel", "hostel", "airbnb", "guesthouse", "night", "nights", "stay", "check-in",
            "room", "resort", "lodging",
        ),
        Category.TRANSPORT to listOf(
            "taxi", "uber", "lyft", "bolt", "bus", "train", "tram", "metro", "subway", "flight",
            "airport", "ferry", "gas", "petrol", "fuel", "parking", "toll", "ticket", "pass",
            "rental", "car",
        ),
    )

    // Matches an amount with an optional leading currency symbol, e.g.
    // "5", "5.50", "€5", "$12.99" — captured as group(1) with symbol group(0)[0] when present.
    private val amountRegex = Regex("""([€$£¥₪])?\s*(\d+(?:[.,]\d{1,2})?)\s*([€$£¥₪])?""")

    fun parse(rawInput: String, fallbackCurrencyCode: String): ParsedExpense? {
        val input = rawInput.trim()
        if (input.isEmpty()) return null

        val amountMatch = amountRegex.findAll(input)
            .firstOrNull { it.groupValues[2].isNotBlank() }
            ?: return null

        val amountText = amountMatch.groupValues[2].replace(',', '.')
        val amount = amountText.toDoubleOrNull() ?: return null
        val amountMinorUnits = Math.round(amount * 100)

        val symbol = amountMatch.groupValues[1].ifBlank { amountMatch.groupValues[3] }
        var currencyCode = symbol.takeIf { it.isNotBlank() }?.let { currencySymbols[it] }
        var currencyConfident = currencyCode != null

        val lowerInput = input.lowercase(Locale.ROOT)
        if (currencyCode == null) {
            val wordMatch = currencyWords.entries.firstOrNull { (word, _) ->
                Regex("\\b${Regex.escape(word)}\\b").containsMatchIn(lowerInput)
            }
            if (wordMatch != null) {
                currencyCode = wordMatch.value
                currencyConfident = true
            }
        }
        if (currencyCode == null) {
            currencyCode = fallbackCurrencyCode
            currencyConfident = false
        }

        // Description = whatever text isn't the amount/currency token, with
        // separators like "-", "—", "for" trimmed off both ends.
        var description = input
            .replace(amountMatch.value, " ")
            .let { text ->
                currencyWords.keys.fold(text) { acc, word ->
                    Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE).replace(acc, " ")
                }
            }
            .trim()
            .trim('-', '—', '–', ',')
            .trim()
            .replace(Regex("^(for|on|at)\\s+", RegexOption.IGNORE_CASE), "")
            .trim()

        if (description.isEmpty()) description = "Expense"
        description = description.replaceFirstChar { it.titlecase(Locale.getDefault()) }

        val category = categoryKeywords.entries.firstOrNull { (_, keywords) ->
            keywords.any { keyword -> Regex("\\b${Regex.escape(keyword)}\\b", RegexOption.IGNORE_CASE).containsMatchIn(lowerInput) }
        }?.key ?: Category.OTHER

        return ParsedExpense(
            description = description,
            amountMinorUnits = amountMinorUnits,
            currencyCode = currencyCode,
            category = category,
            confident = currencyConfident,
        )
    }
}
