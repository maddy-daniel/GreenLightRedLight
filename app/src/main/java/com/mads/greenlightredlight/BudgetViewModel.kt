package com.mads.greenlightredlight

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class BudgetViewModel(private val entryDao: EntryDao, private val context: Context) : ViewModel() {

    private val prefs = context.getSharedPreferences("greenlightredlight_prefs", Context.MODE_PRIVATE)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val entries: StateFlow<List<Entry>> = entryDao.getAllEntries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init{
        checkAndPerformRollover()
    }

    private fun checkAndPerformRollover() {
        val today = LocalDate.now()
        val lastRolloverStr = prefs.getString("last_rollover_date", null)
        val mostRecentSunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val shouldRollover = if(lastRolloverStr == null){
            true
        }
        else{
            val lastRollover = LocalDate.parse(lastRolloverStr, dateFormatter)
            lastRollover.isBefore(mostRecentSunday)
        }
        if(shouldRollover){
            viewModelScope.launch {
                entryDao.deleteIncidentalEntries()
                prefs.edit().putString("last_rollover_date", today.format(dateFormatter)).apply()
            }
        }
    }
    fun addEntry(entry: Entry) {
        viewModelScope.launch {
            entryDao.insertEntry(entry)
        }
    }

    fun deleteEntry(entryId: Int){
        viewModelScope.launch {
            val entry = entries.value.find{it.id == entryId}
            entry?.let {entryDao.deleteEntry(it)}
        }
    }

    fun totalIncome(): Double {
        return entries.value.filter{it.isIncome}.sumOf{
            if(it.isHourly){
                TaxCalculator.calculateNetTakeHome(it.weeklyAmount)
            }
            else{
                it.weeklyAmount
            }
        }
    }

    fun totalExpenses(): Double {
        return entries.value.filter{!it.isIncome}.sumOf{it.weeklyAmount}
    }

    fun netBalance():Double {
        return totalIncome() - totalExpenses()
    }

    fun getEntryById(id: Int): Entry?{
        return entries.value.find{it.id == id}
    }

    fun getEntriesForDate(date: String): List<Entry>{
        return entries.value.filter{
            it.dateAdded == date
        }
    }

    fun getEntriesForWeek(startDate: String, endDate: String): List<Entry>{
        return entries.value.filter{
            it.dateAdded >= startDate && it.dateAdded<= endDate
        }
    }

    fun getWeeklyHistory(): List<WeekSummary>{
        val today = LocalDate.now()
        val currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        return entries.value.filter{
            entry->
            val entryDate = LocalDate.parse(entry.dateAdded)
            val entryWeekStart = entryDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            entryWeekStart.isBefore(currentWeekStart)
        }.groupBy{
            entry->
            val entryDate = LocalDate.parse(entry.dateAdded)
            entryDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        }.map{
            (weekStart,weekEntries)->
            val weekEnd = weekStart.plusDays(6)
            val totalIncome = weekEntries.filter{it.isIncome}.sumOf{
                if(it.isHourly) TaxCalculator.calculateNetTakeHome(it.weeklyAmount)
                else it.weeklyAmount
            }
            val totalExpenses = weekEntries.filter{!it.isIncome }.sumOf{
                it.weeklyAmount
            }
            WeekSummary(
                weekStart = weekStart.toString(),
                weekEnd = weekEnd.toString(),
                entries = weekEntries,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                netBalance = totalIncome - totalExpenses
            )
        }.sortedByDescending{it.weekStart}
    }
}