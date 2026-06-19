package com.jecsdev.papipuntos.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Raw Papi Puntos palette.
 *
 * Source of truth: `papi-puntos-mockup/src/styles.css` (Tailwind v4, OKLCH).
 * Every OKLCH value from the mockup was converted to sRGB for Compose. Token
 * names mirror the CSS variables so they can be traced 1:1.
 */
internal object PapiPuntosPalette {
    // Surfaces / base
    val Canvas = Color(0xFFF6EFF7)      // --canvas
    val Background = Color(0xFFFFFDFF)  // --background
    val Foreground = Color(0xFF181928)  // --foreground
    val Card = Color(0xFFFFFFFF)        // --card
    val Muted = Color(0xFFF8F3F9)       // --muted
    val MutedForeground = Color(0xFF6E7083) // --muted-foreground
    val Border = Color(0xFFECE5EE)      // --border
    val Input = Color(0xFFEFE9F0)       // --input

    // Brand roles
    val Primary = Color(0xFFF996A6)         // --primary (coral, base of gradients)
    val PrimaryForeground = Color(0xFFFCFCFC) // --primary-foreground
    val Secondary = Color(0xFFE2F5FF)       // --secondary
    val SecondaryForeground = Color(0xFF203A64) // --secondary-foreground
    val Accent = Color(0xFFFFE4F2)          // --accent
    val AccentForeground = Color(0xFF641E2E) // --accent-foreground
    val Destructive = Color(0xFFF94144)     // --destructive
    val DestructiveForeground = Color(0xFFFCFCFC) // --destructive-foreground

    // Couple profiles
    val Papi = Color(0xFF4BAEED)        // --papi (blue)
    val PapiSoft = Color(0xFFD4F0FF)    // --papi-soft
    val Mami = Color(0xFFFF8FAB)        // --mami (pink)
    val MamiSoft = Color(0xFFFFE5E9)    // --mami-soft
}

/**
 * Papi Puntos custom color roles that Material 3 does not cover (canvas and the
 * per-profile colors of the couple). Exposed through [LocalPapiPuntosColors] so
 * composables never hardcode hex values.
 */
@Immutable
data class PapiPuntosColors(
    val canvas: Color,
    val papi: Color,
    val papiSoft: Color,
    val mami: Color,
    val mamiSoft: Color,
)

val LightPapiPuntosColors = PapiPuntosColors(
    canvas = PapiPuntosPalette.Canvas,
    papi = PapiPuntosPalette.Papi,
    papiSoft = PapiPuntosPalette.PapiSoft,
    mami = PapiPuntosPalette.Mami,
    mamiSoft = PapiPuntosPalette.MamiSoft,
)
