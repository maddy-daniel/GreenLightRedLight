package com.mads.greenlightredlight

data class WeekSummary(
    val weekStart: String,
    val weekEnd: String,
    val entries: List<Entry>,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netBalance: Double
)
