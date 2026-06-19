package com.jecsdev.papipuntos.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/**
 * Papi Puntos corner-radius scale.
 *
 * The mockup (Tailwind v4) defines `--radius: 1.25rem` (= 20dp) and derives the
 * rest with `calc(var(--radius) ± Npx)`. That config wins over Tailwind's
 * default scale, so here `rounded-2xl` is 28dp and `rounded-3xl` is 32dp.
 *
 * | CSS token    | Tailwind class | dp   |
 * |--------------|----------------|------|
 * | --radius-sm  | rounded-sm     | 16   |
 * | --radius-md  | rounded-md     | 18   |
 * | --radius-lg  | rounded-lg     | 20   |
 * | --radius-xl  | rounded-xl     | 24   |
 * | --radius-2xl | rounded-2xl    | 28   |
 * | --radius-3xl | rounded-3xl    | 32   |
 */
@Immutable
data class PapiPuntosShapes(
    val sm: RoundedCornerShape = RoundedCornerShape(16.dp),
    val md: RoundedCornerShape = RoundedCornerShape(18.dp),
    val lg: RoundedCornerShape = RoundedCornerShape(20.dp),
    val xl: RoundedCornerShape = RoundedCornerShape(24.dp),
    val xxl: RoundedCornerShape = RoundedCornerShape(28.dp),
    val xxxl: RoundedCornerShape = RoundedCornerShape(32.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(percent = 50),
)

val DefaultPapiPuntosShapes = PapiPuntosShapes()

/**
 * Mapping onto the Material 3 shape slots so any Material component picks up the
 * correct radii by default.
 */
val PapiPuntosMaterialShapes = Shapes(
    extraSmall = DefaultPapiPuntosShapes.sm,
    small = DefaultPapiPuntosShapes.md,
    medium = DefaultPapiPuntosShapes.lg,
    large = DefaultPapiPuntosShapes.xl,
    extraLarge = DefaultPapiPuntosShapes.xxl,
)
