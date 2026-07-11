package com.example.myapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapp.data.db.dao.CustomColorThemeDao
import com.example.myapp.data.db.entities.CustomColorTheme

@Database(
    entities = [CustomColorTheme::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customColorThemeDao(): CustomColorThemeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Version 2: adds exact ARGB columns for the three colour roles and the
         * per-mode background overrides. 0 means "unset"; loaders fall back to
         * the legacy hue columns, so existing rows keep working.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE custom_color_themes ADD COLUMN primaryArgb INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE custom_color_themes ADD COLUMN secondaryArgb INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE custom_color_themes ADD COLUMN tertiaryArgb INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE custom_color_themes ADD COLUMN lightBackgroundArgb INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE custom_color_themes ADD COLUMN darkBackgroundArgb INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myapp.db",
                )
                    // No fallbackToDestructiveMigration — add explicit migrations for future versions.
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
