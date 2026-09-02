package com.tripbudget.app

import android.app.Application
import com.tripbudget.app.data.AppDatabase
import com.tripbudget.app.data.ExpenseRepository
import com.tripbudget.app.data.TripRepository

class TripBudgetApp : Application() {

    lateinit var tripRepository: TripRepository
        private set
    lateinit var expenseRepository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        tripRepository = TripRepository(db.tripDao())
        expenseRepository = ExpenseRepository(db.expenseDao())
    }
}
