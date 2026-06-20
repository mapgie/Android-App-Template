package com.example.myapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapp.data.db.dao.CustomColorThemeDao
import com.example.myapp.data.db.entities.CustomColorTheme

@Database(
    entities = [CustomColorTheme::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customColorThemeDao(): CustomColorThemeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myapp.db",
                )
                    // No fallbackToDestructiveMigration — add explicit migrations for future versions.
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
