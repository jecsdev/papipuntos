package com.jecsdev.papipuntos.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Papi Puntos font family.
 *
 * The mockup uses `--font-display: "Plus Jakarta Sans"`. Until the TTFs are
 * dropped into `commonMain/composeResources/font/`, this falls back to the
 * system font; switching to the real font is a one-line change here (return the
 * `FontFamily` built from `Font(Res.font.plusJakartaSans_*)`).
 */
val PapiPuntosFontFamily: FontFamily = FontFamily.Default

/**
 * Type scale derived from the mockup. Weights used:
 * extrabold (800), bold (700), semibold (600), medium (500).
 * Titles carry `tracking-tight` (≈ -0.025em).
 */
val PapiPuntosTypography = Typography().run {
    val tight = (-0.025).em
    copy(
        // Large scoreboard number (text-3xl font-extrabold)
        displaySmall = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            letterSpacing = tight,
        ),
        // Screen title "Marcador de la pareja" (text-[22px] font-extrabold)
        headlineSmall = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = tight,
        ),
        // Section titles / TopBar (text-lg font-extrabold)
        titleLarge = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = tight,
        ),
        // List headers / large button (text-base font-bold)
        titleMedium = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        // Primary item label (text-sm font-semibold/bold)
        titleSmall = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Base paragraph text (text-sm)
        bodyMedium = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Subtext / timestamps (text-xs)
        bodySmall = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Button / CTA text (text-sm font-bold)
        labelLarge = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
        // Badges / chips (text-xs font-bold)
        labelMedium = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Tab bar labels / micro-text (text-[10px] font-semibold)
        labelSmall = TextStyle(
            fontFamily = PapiPuntosFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
        ),
    )
}
