package com.mads.greenlightredlight

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")

data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "weekly_amount")
    val weeklyAmount: Double,
    @ColumnInfo(name = "is_income")
    val isIncome: Boolean,
    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean,
    @ColumnInfo(name = "is_hourly")
    val isHourly: Boolean = false,
    @ColumnInfo(name = "frequency")
    val frequency: String = "Weekly"
)