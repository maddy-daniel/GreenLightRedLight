package com.mads.greenlightredlight

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//@Database: Tells Room this is the main database class and which tables (entities) it contains
@Database(entities = [Entry::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao               //Provides access to the DAO

    //Companion Object: Implements the singleton pattern so only one instance of the database ever exists
    companion object {
        @Volatile                                       //Ensures the instance is always up to date across threads
        private var INSTANCE:AppDatabase? = null

        fun getInstance (context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){          //Prevents two threads from creating the database at the same time
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "greenlightredlight_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}