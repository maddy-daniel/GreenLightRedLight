package com.example.greenlightredlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(private val entryDao: EntryDao) : ViewModel() {

    val entries: StateFlow<List<Entry>> = entryDao.getAllEntries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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
        return entries.value.filter{it.isIncome}.sumOf{it.weeklyAmount}
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
}