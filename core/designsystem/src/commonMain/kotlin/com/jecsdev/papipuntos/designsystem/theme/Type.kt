package com.jecsdev.papipuntos.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import papipuntos.core.designsystem.generated.resources.Res
import papipuntos.core.designsystem.generated.resources.plus_jakarta_sans_bold
import papipuntos.core.designsystem.generated.resources.plus_jakarta_sans_extrabold
import papipuntos.core.designsystem.generated.resources.plus_jakarta_sans_medium
import papipuntos.core.designsystem.generated.resources.plus_jakarta_sans_regular
import papipuntos.core.designsystem.generated.resources.plus_jakarta_sans_semibold

/**
 * Papi Puntos font family — "Plus Jakarta Sans" (mockup `--font-display`),
 * loaded from the bundled TTFs. Must be read in composition because the
 * compose-resources `Font` loader is `@Composable`.
 */
@Composable
fun rememberPapiPuntosFontFamily(): FontFamily = FontFamily(
    Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(Res.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

/** Remembers the full type scale bound to the Plus Jakarta Sans family. */
@Composable
fun rememberPapiPuntosTypography(): Typography {
    val fontFamily = rememberPapiPuntosFontFamily()
    return remember(fontFamily) { papiPuntosTypography(fontFamily) }
}

/**
 * Type scale derived from the mockup. Weights used:
 * extrabold (800), bold (700), semibold (600), medium (500).
 * Titles carry `tracking-tight` (≈ -0.025em).
 */
fun papiPuntosTypography(fontFamily: FontFamily): Typography {
    val tight = (-0.025).em
    return Typography().copy(
        // Large scoreboard number (text-3xl font-extrabold)
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            letterSpacing = tight,
        ),
        // Screen title "Marcador de la pareja" (text-[22px] font-extrabold)
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = tight,
        ),
        // Section titles / TopBar (text-lg font-extrabold)
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = tight,
        ),
        // List headers / large button (text-base font-bold)
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        // Primary item label (text-sm font-semibold/bold)
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Base paragraph text (text-sm)
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Subtext / timestamps (text-xs)
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Button / CTA text (text-sm font-bold)
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
        // Badges / chips (text-xs font-bold)
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Tab bar labels / micro-text (text-[10px] font-semibold)
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
        ),
    )
}
