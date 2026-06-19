package com.jecsdev.papipuntos.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Material 3 [androidx.compose.material3.ColorScheme] fed with the mockup
 * tokens. Roles Material does not have (canvas, papi, mami…) travel separately
 * in [LocalPapiPuntosColors].
 */
private val PapiPuntosColorScheme = lightColorScheme(
    primary = PapiPuntosPalette.Primary,
    onPrimary = PapiPuntosPalette.PrimaryForeground,
    secondary = PapiPuntosPalette.Secondary,
    onSecondary = PapiPuntosPalette.SecondaryForeground,
    tertiary = PapiPuntosPalette.Accent,
    onTertiary = PapiPuntosPalette.AccentForeground,
    background = PapiPuntosPalette.Background,
    onBackground = PapiPuntosPalette.Foreground,
    surface = PapiPuntosPalette.Card,
    onSurface = PapiPuntosPalette.Foreground,
    surfaceVariant = PapiPuntosPalette.Muted,
    onSurfaceVariant = PapiPuntosPalette.MutedForeground,
    error = PapiPuntosPalette.Destructive,
    onError = PapiPuntosPalette.DestructiveForeground,
    outline = PapiPuntosPalette.Border,
    outlineVariant = PapiPuntosPalette.Input,
)

internal val LocalPapiPuntosColors = staticCompositionLocalOf { LightPapiPuntosColors }
internal val LocalPapiPuntosShapes = staticCompositionLocalOf { DefaultPapiPuntosShapes }

/**
 * Root theme of the app. Wraps `MaterialTheme` and publishes the custom roles.
 *
 * Access from composables:
 * - Material:  `MaterialTheme.colorScheme`, `MaterialTheme.typography`
 * - Custom:    `PapiPuntosTheme.colors`, `PapiPuntosTheme.shapes`
 */
@Composable
fun PapiPuntosTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalPapiPuntosColors provides LightPapiPuntosColors,
        LocalPapiPuntosShapes provides DefaultPapiPuntosShapes,
    ) {
        MaterialTheme(
            colorScheme = PapiPuntosColorScheme,
            typography = PapiPuntosTypography,
            shapes = PapiPuntosMaterialShapes,
            content = content,
        )
    }
}

/** Accessor for the custom tokens, mirroring the `MaterialTheme` object style. */
object PapiPuntosTheme {
    val colors: PapiPuntosColors
        @Composable @ReadOnlyComposable get() = LocalPapiPuntosColors.current

    val shapes: PapiPuntosShapes
        @Composable @ReadOnlyComposable get() = LocalPapiPuntosShapes.current

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val materialShapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes
}
