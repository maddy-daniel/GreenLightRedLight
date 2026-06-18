package com.mads.greenlightredlight

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//@Database: Tells Room this is the main database class and which tables (entities) it contains
@Database(entities = [Entry::class], version = 2, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao               //Provides access to the DAO

    //companion object: Implements the singleton pattern so only one instance of the database ever exists
    companion object {
        @Volatile                                       //Ensures the instance is always up to date across threads
        private var INSTANCE:AppDatabase? = null

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE entries ADD COLUMN date_added TEXT NOT NULL DEFAULT ''"
                )
            }
        }
        fun getInstance (context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){          //Prevents two threads from creating the database at the same time
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "greenlightredlight_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}