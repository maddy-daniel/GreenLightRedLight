package com.example.greenlightredlight

data class Entry(
    val id: Int,
    val name: String,
    val amount: Double,
    val weeklyAmount: Double,
    val isIncome: Boolean,
    val isRecurring: Boolean,
    val isHourly: Boolean = false,
    val frequency: String = "Weekly"
)