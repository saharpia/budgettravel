package com.tripbudget.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A category is intentionally a fixed, small enum rather than a user-defined
 * table — the whole point of the app is zero-friction entry, and an open
 * category picker is friction. "Other" is the escape hatch.
 */
enum class Category(val displayName: String) {
    FOOD("Food & drink"),
    STAYS("Stays"),
    TRANSPORT("Getting around"),
    OTHER("Other"),
}

enum class ExpenseSource {
    TYPED,
    RECEIPT_PHOTO,
    PDF_IMPORT,
}

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDateEpochDay: Long,
    val endDateEpochDay: Long,
    val budgetMinorUnits: Long, // stored in the currency's smallest unit (cents) to avoid float drift
    val currencyCode: String, // ISO 4217, e.g. "EUR"
    val isActive: Boolean = true,
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val description: String,
    val amountMinorUnits: Long,
    val currencyCode: String,
    val category: Category,
    val source: ExpenseSource,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
    /** True until the record has been reconciled with a server. This project
     * has no backend wired up yet — it exists so offline-first sync can be
     * added later without touching the schema. */
    val pendingSync: Boolean = true,
    /** Set when this expense came from a receipt photo or PDF, so the
     * original can be reopened from the expense detail view. */
    val attachmentPath: String? = null,
)
