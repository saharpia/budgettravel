package com.tripbudget.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Room already gives us offline-first behavior for free — every read and
 * write here hits only the local database, no network round trip. The
 * `pendingSync` flag on Expense and a WorkManager job (see
 * `sync/SyncWorker.kt` — not implemented in this scaffold) are the seam for
 * adding a backend later without changing any screen code.
 */
class TripRepository(private val tripDao: TripDao) {
    fun observeActiveTrip(): Flow<Trip?> = tripDao.observeActiveTrip()
    fun observeAllTrips(): Flow<List<Trip>> = tripDao.observeAllTrips()

    suspend fun createTrip(trip: Trip): Long = tripDao.insert(trip)
    suspend fun updateTrip(trip: Trip) = tripDao.update(trip)
    suspend fun getTrip(id: Long): Trip? = tripDao.getById(id)
}

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun observeForTrip(tripId: Long): Flow<List<Expense>> = expenseDao.observeForTrip(tripId)
    fun observeTotalSpent(tripId: Long): Flow<Long> = expenseDao.observeTotalSpentMinorUnits(tripId)
    fun observeCategoryTotals(tripId: Long): Flow<List<CategoryTotal>> = expenseDao.observeCategoryTotals(tripId)

    suspend fun add(expense: Expense): Long = expenseDao.insert(expense)
    suspend fun update(expense: Expense) = expenseDao.update(expense)
    suspend fun delete(id: Long) = expenseDao.delete(id)
}
