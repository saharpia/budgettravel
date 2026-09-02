package com.tripbudget.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun categoryToString(value: Category): String = value.name

    @TypeConverter
    fun stringToCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun sourceToString(value: ExpenseSource): String = value.name

    @TypeConverter
    fun stringToSource(value: String): ExpenseSource = ExpenseSource.valueOf(value)
}
