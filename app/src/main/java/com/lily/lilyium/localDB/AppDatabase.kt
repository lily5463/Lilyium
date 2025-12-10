package com.lily.lilyium.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lily.lilyium.localDB.model.testSong
import com.lily.lilyium.localDB.service.DatabaseDao


@Database(
    entities = [testSong::class],
    version = 1,
    exportSchema = false
)
    @TypeConverters(Converters::class)
    abstract class AppDatabase : RoomDatabase() {

    // Your DAO
    abstract fun DatabaseDao(): DatabaseDao


    //    Make this a singleton class
        companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lilyium_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}