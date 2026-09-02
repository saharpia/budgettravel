package com.tripbudget.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)

    @Query("SELECT * FROM trips WHERE isActive = 1 LIMIT 1")
    fun observeActiveTrip(): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getById(tripId: Long): Trip?

    @Query("SELECT * FROM trips ORDER BY startDateEpochDay DESC")
    fun observeAllTrips(): Flow<List<Trip>>
}

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun delete(expenseId: Long)

    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY occurredAtEpochMillis DESC")
    fun observeForTrip(tripId: Long): Flow<List<Expense>>

    @Query(
        "SELECT * FROM expenses WHERE tripId = :tripId " +
            "AND occurredAtEpochMillis BETWEEN :startMillis AND :endMillis " +
            "ORDER BY occurredAtEpochMillis DESC"
    )
    fun observeForTripOnDay(tripId: Long, startMillis: Long, endMillis: Long): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amountMinorUnits), 0) FROM expenses WHERE tripId = :tripId")
    fun observeTotalSpentMinorUnits(tripId: Long): Flow<Long>

    @Query(
        "SELECT category AS category, COALESCE(SUM(amountMinorUnits), 0) AS totalMinorUnits " +
            "FROM expenses WHERE tripId = :tripId GROUP BY category"
    )
    fun observeCategoryTotals(tripId: Long): Flow<List<CategoryTotal>>
}

data class CategoryTotal(
    val category: Category,
    val totalMinorUnits: Long,
)
