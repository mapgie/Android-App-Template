package com.example.myapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myapp.data.db.entities.CustomColorTheme

/**
 * Shows a list of user-saved custom colour themes with Load / Delete actions.
 *
 * Each row displays:
 * - A 3-circle colour swatch (primary, secondary, tertiary derived from the saved hues)
 * - The theme name (highlighted when active)
 * - A "Load" icon button
 * - A "Delete" icon button (with a confirmation dialog)
 *
 * Long-pressing a row reveals an inline rename field.
 *
 * @param themes           The list of saved themes to display.
 * @param activeProfileId  The ID of the currently active profile (-1 for none).
 * @param onLoad           Called when the user taps "Load" for a theme.
 * @param onDelete         Called after the user confirms deletion of a theme.
 * @param onRename         Called when the user submits a new name for a theme.
 */
@Composable
fun SavedThemesList(
    themes: List<CustomColorTheme>,
    activeProfileId: Long,
    onLoad: (CustomColorTheme) -> Unit,
    onDelete: (CustomColorTheme) -> Unit,
    onRename: (CustomColorTheme, String) -> Unit,
) {
    if (themes.isEmpty()) {
        Text(
            text  = "No saved themes yet",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        themes.forEach { theme ->
            SavedThemeRow(
                theme           = theme,
                isActive        = theme.id == activeProfileId,
                onLoad          = { onLoad(theme) },
                onDelete        = { onDelete(theme) },
                onRename        = { newName -> onRename(theme, newName) },
            )
        }
    }
}

// ── Single saved-theme row ─────────────────────────────────────────────────────

@Composable
private fun SavedThemeRow(
    theme:    CustomColorTheme,
    isActive: Boolean,
    onLoad:   () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit,
) {
    var renaming       by rememberSaveable { mutableStateOf(false) }
    var renameText     by rememberSaveable(theme.name) { mutableStateOf(theme.name) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    val focusManager   = LocalFocusManager.current

    val activeColor    = MaterialTheme.colorScheme.primary
    val borderColor    = if (isActive) activeColor else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (isActive)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { role = Role.Button }
                .combinedClickable(
                    onClick      = { /* row tap: no-op; actions are in the icon buttons */ },
                    onLongClick  = { renaming = true },
                    onLongClickLabel = "Rename theme",
                )
                .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Three colour swatches
            ThemeSwatches(
                primaryHue   = theme.primaryHue,
                secondaryHue = theme.secondaryHue,
                tertiaryHue  = theme.tertiaryHue,
            )

            Spacer(Modifier.width(10.dp))

            // Theme name (expands to fill)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = theme.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurface,
                )
                if (isActive) {
                    Text(
                        text  = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = activeColor,
                    )
                }
            }

            // Load button
            IconButton(
                onClick  = onLoad,
                modifier = Modifier.semantics {
                    contentDescription = "Load theme ${theme.name}"
                    role = Role.Button
                },
            ) {
                Icon(
                    imageVector        = if (isActive) Icons.Default.Check else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint               = if (isActive) activeColor else MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp),
                )
            }

            // Delete button
            IconButton(
                onClick  = { showDeleteConfirm = true },
                modifier = Modifier.semantics {
                    contentDescription = "Delete theme ${theme.name}"
                    role = Role.Button
                },
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error,
                    modifier           = Modifier.size(20.dp),
                )
            }
        }

        // Inline rename field (shown after long-press)
        if (renaming) {
            Row(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value          = renameText,
                    onValueChange  = { renameText = it },
                    singleLine     = true,
                    label          = { Text("Theme name") },
                    keyboardOptions   = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions   = KeyboardActions(onDone = {
                        val trimmed = renameText.trim()
                        if (trimmed.isNotEmpty()) onRename(trimmed)
                        renaming = false
                        focusManager.clearFocus()
                    }),
                    modifier       = Modifier.weight(1f),
                )
                TextButton(
                    onClick = {
                        val trimmed = renameText.trim()
                        if (trimmed.isNotEmpty()) onRename(trimmed)
                        renaming = false
                        focusManager.clearFocus()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title            = { Text("Delete theme?") },
            text             = { Text("\"${theme.name}\" will be removed. This cannot be undone.") },
            confirmButton    = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) { Text("Delete") }
            },
            dismissButton    = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}

// ── Colour swatch helper ───────────────────────────────────────────────────────

/**
 * Three small overlapping circles showing the primary, secondary, and tertiary
 * hues of a saved custom theme.
 */
@Composable
private fun ThemeSwatches(
    primaryHue:   Float,
    secondaryHue: Float,
    tertiaryHue:  Float,
) {
    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
        HueSwatch(hue = primaryHue,   saturation = 0.60f, lightness = 0.35f)
        HueSwatch(hue = secondaryHue, saturation = 0.45f, lightness = 0.35f)
        HueSwatch(hue = tertiaryHue,  saturation = 0.45f, lightness = 0.35f)
    }
}

@Composable
private fun HueSwatch(hue: Float, saturation: Float, lightness: Float) {
    val color    = Color.hsl(hue, saturation, lightness)
    val outline  = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, outline, CircleShape)
    )
}
