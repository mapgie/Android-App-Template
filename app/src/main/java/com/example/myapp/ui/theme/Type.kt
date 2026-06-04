package com.example.myapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.myapp.R

// ── Google Font provider ──────────────────────────────────────────────────────
// Uses the system's downloadable-font provider (no bundled binary needed).
// Falls back to the default system font if Google Play Services is unavailable.

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs,
)

// Replace "Comfortaa" with your own brand font name from fonts.google.com.
// The Bold weight is used for display text, labels, and FABs.
val BrandFontFamily: FontFamily = FontFamily(
    Font(
        googleFont   = GoogleFont("Comfortaa"),
        fontProvider = provider,
        weight       = FontWeight.Bold,
    )
)

// ── App typography ────────────────────────────────────────────────────────────
// Omit `color` from all TextStyle entries — let MaterialTheme propagate it.
// Hardcoded colours break contrast in non-default themes.

val MyAppTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
)
