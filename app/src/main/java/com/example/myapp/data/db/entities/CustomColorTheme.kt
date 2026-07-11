package com.example.myapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A user-saved custom colour theme.
 *
 * The exact picked colours live in the ARGB columns (0 = unset). The hue
 * columns are the legacy pre-ARGB representation and act as fallbacks when the
 * matching ARGB column is 0, so profiles saved by older versions still load.
 * [mode] is one of "LIGHT", "DARK", or "SYSTEM" and records which mode was
 * active when the theme was saved; it is restored on load.
 */
@Entity(tableName = "custom_color_themes")
data class CustomColorTheme(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val primaryHue: Float,
    val secondaryHue: Float,
    val tertiaryHue: Float,
    val mode: String,  // "LIGHT", "DARK", or "SYSTEM"
    val primaryArgb: Int = 0,
    val secondaryArgb: Int = 0,
    val tertiaryArgb: Int = 0,
    val lightBackgroundArgb: Int = 0,
    val darkBackgroundArgb: Int = 0,
)
