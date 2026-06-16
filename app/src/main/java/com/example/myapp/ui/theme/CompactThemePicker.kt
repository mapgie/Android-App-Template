package com.example.myapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.myapp.ui.screens.settings.SwitchRow

// ── Theme mode ────────────────────────────────────────────────────────────────

private enum class ThemeMode(val label: String) {
    LIGHT("Light"), DARK("Dark"), SYSTEM("Auto")
}

// ── Standard palette catalogue ────────────────────────────────────────────────
//
// Maps named palettes to their light, dark, and system AppTheme variants.
// previewArgb is the primary colour; accentArgb is the tertiary/accent colour,
// used to draw the lower-right half of the 2-tone palette circle.

private enum class StandardPalette(
    val displayName: String,
    val lightTheme: AppTheme,
    val darkTheme: AppTheme,
    val systemTheme: AppTheme,
    val previewArgb: Long,
    val accentArgb: Long,
) {
    // Classic
    CORAL        ("Coral",               AppTheme.CORAL,        AppTheme.CORAL_DARK,        AppTheme.CORAL_SYSTEM,        0xFFC15542L, 0xFFB5307AL),
    TEAL         ("Teal",                AppTheme.TURQUOISE,    AppTheme.TURQUOISE_DARK,    AppTheme.SYSTEM,              0xFF00696FL, 0xFF4E6078L),
    SAGE         ("Sage",                AppTheme.GREEN,        AppTheme.GREEN_DARK,        AppTheme.GREEN_SYSTEM,        0xFF386A20L, 0xFF6B5BAEL),
    // Fun
    SUMMER_CANDY ("Summer Candy",        AppTheme.SUMMER_CANDY, AppTheme.SUMMER_CANDY_DARK, AppTheme.SUMMER_CANDY_SYSTEM, 0xFFC2185BL, 0xFF9B27AFL),
    BEACH_VIBES  ("Beach Vibes",         AppTheme.BEACH_VIBES,  AppTheme.BEACH_VIBES_DARK,  AppTheme.BEACH_VIBES_SYSTEM,  0xFF0D47A1L, 0xFFD4700AL),
    PEACH_MELBA  ("Peach Melba",         AppTheme.PEACH_MELBA,  AppTheme.PEACH_MELBA_DARK,  AppTheme.PEACH_MELBA_SYSTEM,  0xFFBF360CL, 0xFF9C5BA0L),
    DISCO        ("Disco Party",         AppTheme.DISCO,        AppTheme.DISCO_DARK,        AppTheme.DISCO_SYSTEM,        0xFF7B0EA0L, 0xFF76B900L),
    METAL_CHICK  ("Metal Chic",          AppTheme.METAL_CHICK,  AppTheme.METAL_CHICK_DARK,  AppTheme.METAL_CHICK_SYSTEM,  0xFF4A4A5AL, 0xFF6B2D3EL),
    WHIMSY       ("Whimsy",              AppTheme.WHIMSY,       AppTheme.WHIMSY_DARK,       AppTheme.WHIMSY_SYSTEM,       0xFF5050A0L, 0xFF2D7A6EL),
    COLOUR_HAPPY ("Colour Me Happy",     AppTheme.COLOUR_HAPPY, AppTheme.COLOUR_HAPPY_DARK, AppTheme.COLOUR_HAPPY_SYSTEM, 0xFFD63A26L, 0xFF00A8E8L),
    DRAGON_FIRE  ("Dragon Fire",         AppTheme.DRAGON_FIRE,  AppTheme.DRAGON_FIRE_DARK,  AppTheme.DRAGON_FIRE_SYSTEM,  0xFFB71C1CL, 0xFFE07800L),
    MIDNIGHT_NEON("Midnight Neon",       AppTheme.MIDNIGHT_NEON,AppTheme.MIDNIGHT_NEON_DARK,AppTheme.MIDNIGHT_NEON_SYSTEM,0xFF6200EAL, 0xFF76B900L),
    // Accessibility
    MAX_CONTRAST ("Max Contrast",        AppTheme.HIGH_CONTRAST_LIGHT, AppTheme.HIGH_CONTRAST_DARK, AppTheme.HIGH_CONTRAST_LIGHT, 0xFF1A1A1AL, 0xFF000000L),
    BLUE_ORANGE  ("Blue & Orange",       AppTheme.BLUE_ORANGE,  AppTheme.BLUE_ORANGE,       AppTheme.BLUE_ORANGE,         0xFF005FADL, 0xFF8B5000L),
}

// ── ThemeMode extension — derived from AppTheme ───────────────────────────────

private val AppTheme.themeMode: ThemeMode? get() = when (this) {
    AppTheme.SYSTEM,
    AppTheme.CORAL_SYSTEM,
    AppTheme.GREEN_SYSTEM,
    AppTheme.SUMMER_CANDY_SYSTEM,
    AppTheme.BEACH_VIBES_SYSTEM,
    AppTheme.PEACH_MELBA_SYSTEM,
    AppTheme.DISCO_SYSTEM,
    AppTheme.METAL_CHICK_SYSTEM,
    AppTheme.WHIMSY_SYSTEM,
    AppTheme.COLOUR_HAPPY_SYSTEM,
    AppTheme.DRAGON_FIRE_SYSTEM,
    AppTheme.MIDNIGHT_NEON_SYSTEM,
    AppTheme.HIGH_CONTRAST_LIGHT -> ThemeMode.SYSTEM
    AppTheme.CORAL_DARK,
    AppTheme.TURQUOISE_DARK,
    AppTheme.GREEN_DARK,
    AppTheme.SUMMER_CANDY_DARK,
    AppTheme.BEACH_VIBES_DARK,
    AppTheme.PEACH_MELBA_DARK,
    AppTheme.DISCO_DARK,
    AppTheme.METAL_CHICK_DARK,
    AppTheme.WHIMSY_DARK,
    AppTheme.COLOUR_HAPPY_DARK,
    AppTheme.DRAGON_FIRE_DARK,
    AppTheme.MIDNIGHT_NEON_DARK,
    AppTheme.HIGH_CONTRAST_DARK  -> ThemeMode.DARK
    else                         -> ThemeMode.LIGHT
}

private val AppTheme.standardPalette: StandardPalette? get() = when (this) {
    AppTheme.CORAL, AppTheme.CORAL_DARK, AppTheme.CORAL_SYSTEM                             -> StandardPalette.CORAL
    AppTheme.TURQUOISE, AppTheme.TURQUOISE_DARK, AppTheme.SYSTEM                           -> StandardPalette.TEAL
    AppTheme.GREEN, AppTheme.GREEN_DARK, AppTheme.GREEN_SYSTEM                             -> StandardPalette.SAGE
    AppTheme.SUMMER_CANDY, AppTheme.SUMMER_CANDY_DARK, AppTheme.SUMMER_CANDY_SYSTEM        -> StandardPalette.SUMMER_CANDY
    AppTheme.BEACH_VIBES, AppTheme.BEACH_VIBES_DARK, AppTheme.BEACH_VIBES_SYSTEM           -> StandardPalette.BEACH_VIBES
    AppTheme.PEACH_MELBA, AppTheme.PEACH_MELBA_DARK, AppTheme.PEACH_MELBA_SYSTEM           -> StandardPalette.PEACH_MELBA
    AppTheme.DISCO, AppTheme.DISCO_DARK, AppTheme.DISCO_SYSTEM                             -> StandardPalette.DISCO
    AppTheme.METAL_CHICK, AppTheme.METAL_CHICK_DARK, AppTheme.METAL_CHICK_SYSTEM           -> StandardPalette.METAL_CHICK
    AppTheme.WHIMSY, AppTheme.WHIMSY_DARK, AppTheme.WHIMSY_SYSTEM                         -> StandardPalette.WHIMSY
    AppTheme.COLOUR_HAPPY, AppTheme.COLOUR_HAPPY_DARK, AppTheme.COLOUR_HAPPY_SYSTEM        -> StandardPalette.COLOUR_HAPPY
    AppTheme.DRAGON_FIRE, AppTheme.DRAGON_FIRE_DARK, AppTheme.DRAGON_FIRE_SYSTEM           -> StandardPalette.DRAGON_FIRE
    AppTheme.MIDNIGHT_NEON, AppTheme.MIDNIGHT_NEON_DARK, AppTheme.MIDNIGHT_NEON_SYSTEM     -> StandardPalette.MIDNIGHT_NEON
    AppTheme.HIGH_CONTRAST_LIGHT, AppTheme.HIGH_CONTRAST_DARK                             -> StandardPalette.MAX_CONTRAST
    AppTheme.BLUE_ORANGE                                                                   -> StandardPalette.BLUE_ORANGE
    AppTheme.CUSTOM                                                                        -> null
}

// ── CompactThemePicker ────────────────────────────────────────────────────────

/**
 * A self-contained theme picker for use in Settings > Appearance.
 *
 * Shows a Light / Dark / Auto mode row, a WCAG accessibility toggle, and a
 * 4-column palette grid. Changing the mode re-selects the same palette in the
 * new mode. Changing the palette keeps the current mode.
 *
 * @param currentTheme  The currently active [AppTheme].
 * @param wcagMode      Whether WCAG high-contrast adjustment is enabled.
 * @param onThemeSelected  Called when the user taps a palette or switches mode.
 * @param onWcagToggled    Called when the user toggles the WCAG switch.
 */
@Composable
fun CompactThemePicker(
    currentTheme: AppTheme,
    wcagMode: Boolean,
    onThemeSelected: (AppTheme) -> Unit,
    onWcagToggled: (Boolean) -> Unit,
) {
    val currentMode    = currentTheme.themeMode ?: ThemeMode.LIGHT
    val currentPalette = currentTheme.standardPalette

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // ── Mode selector ─────────────────────────────────────────────────────
        Text(
            "Mode",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ) {
            val modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)
            modes.forEachIndexed { index, mode ->
                val selected = mode == currentMode
                val modeIcon = when (mode) {
                    ThemeMode.LIGHT  -> Icons.Outlined.WbSunny
                    ThemeMode.DARK   -> Icons.Outlined.DarkMode
                    ThemeMode.SYSTEM -> Icons.Outlined.SettingsBrightness
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .semantics { role = Role.RadioButton }
                        .clickable {
                            val palette = currentPalette ?: StandardPalette.TEAL
                            onThemeSelected(
                                when (mode) {
                                    ThemeMode.DARK   -> palette.darkTheme
                                    ThemeMode.SYSTEM -> palette.systemTheme
                                    ThemeMode.LIGHT  -> palette.lightTheme
                                }
                            )
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector        = modeIcon,
                            contentDescription = mode.label,
                            tint               = if (selected) MaterialTheme.colorScheme.onPrimary
                                                 else MaterialTheme.colorScheme.onSurface,
                            modifier           = Modifier.size(18.dp),
                        )
                        Text(
                            text  = mode.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                if (index < modes.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }

        // ── WCAG toggle ───────────────────────────────────────────────────────
        SwitchRow(
            label           = "WCAG accessible colours",
            supportingText  = "Increases contrast for text and interactive elements",
            checked         = wcagMode,
            onCheckedChange = onWcagToggled,
        )

        // ── Colour grid ───────────────────────────────────────────────────────
        Text(
            "Colour",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // 4-column grid of palette circles. Fill incomplete rows with spacers.
        val allPalettes = StandardPalette.entries
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            allPalettes.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { palette ->
                        PaletteOption(
                            palette  = palette,
                            selected = palette == currentPalette,
                            onClick  = {
                                onThemeSelected(
                                    when (currentMode) {
                                        ThemeMode.DARK   -> palette.darkTheme
                                        ThemeMode.SYSTEM -> palette.systemTheme
                                        ThemeMode.LIGHT  -> palette.lightTheme
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

// ── Palette circle ────────────────────────────────────────────────────────────

@Composable
private fun PaletteOption(
    palette:  StandardPalette,
    selected: Boolean,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .semantics { role = Role.RadioButton }
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val primaryColor = Color(palette.previewArgb)
        val accentColor  = Color(palette.accentArgb)
        val selRingColor = MaterialTheme.colorScheme.primary
        val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

        Box(
            modifier         = Modifier.size(44.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(40.dp)) {
                val w = size.width
                val h = size.height
                val r = w / 2f

                // 2-tone circle: primary upper-left, accent lower-right
                androidx.compose.ui.graphics.drawscope.clipPath(
                    Path().apply { addOval(Rect(0f, 0f, w, h)) }
                ) {
                    drawPath(
                        path  = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(0f, h); close() },
                        color = primaryColor,
                    )
                    drawPath(
                        path  = Path().apply { moveTo(w, 0f); lineTo(w, h); lineTo(0f, h); close() },
                        color = accentColor,
                    )
                }

                val strokePx = if (selected) 3.dp.toPx() else 1.dp.toPx()
                drawCircle(
                    color  = if (selected) selRingColor else outlineColor,
                    radius = r - strokePx / 2f,
                    style  = Stroke(width = strokePx),
                )
            }

            if (selected) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
        }

        Text(
            text  = palette.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
