package com.mads.greenlightredlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

class ViewModelFactory(private  val entryDao: EntryDao, private val context: Context) : ViewModelProvider.Factory {       //ViewModelProvider.Factory: An Android interface that tells the system how to create your ViewModel
                                                                                            //entryDao: Pass the DAO into the ViewModel so it can read and write to the database
    override fun <T : ViewModel> create(modelClass: Class<T>): T {                  //create() function: Android calls when it needs a new BudgetViewModel
        if(modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")                                    //@Suppress("UNCHECKED_CAST"): Suppresses a Kotlin compiler warning about the type case, it's safe to ignore
            return BudgetViewModel(entryDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}