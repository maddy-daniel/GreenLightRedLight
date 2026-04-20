package com.example.greenlightredlight

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BudgetViewModel : ViewModel() {
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())
    val entries: StateFlow<List<Entry>> = _entries

    private var nextId = 1

    fun addEntry(entry: Entry) {
        _entries.value = _entries.value+ entry.copy(id = nextId++)
    }

    fun deleteEntry(entryId: Int){
        _entries.value = _entries.value.filter{it.id != entryId}
    }

    fun totalIncome(): Double {
        return _entries.value.filter { it.isIncome }.sumOf{ it.weeklyAmount }
    }

    fun totalExpenses(): Double {
        return _entries.value.filter { !it.isIncome }.sumOf{ it.weeklyAmount }
    }

    fun netBalance():Double {
        return totalIncome() - totalExpenses()
    }

    fun getEntryById(id: Int): Entry?{
        return _entries.value.find{it.id == id}
    }
}