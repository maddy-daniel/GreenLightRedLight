package com.mads.greenlightredlight

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("SELECT * FROM entries")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("DELETE FROM entries WHERE is_recurring = 0")
    suspend fun deleteIncidentalEntries()

    @Query("SELECT * FROM entries WHERE date_added = :date")
    fun getEntriesByDate(date:String):Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE date_added BETWEEN :startDate AND :endDate")
    fun getEntriesByWeek(startDate: String, endDate: String): Flow<List<Entry>>
}