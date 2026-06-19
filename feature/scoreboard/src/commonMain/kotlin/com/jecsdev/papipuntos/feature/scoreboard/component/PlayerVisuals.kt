package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.model.Player

/**
 * Maps a [Player] to its theme colors and display copy. Centralizes the
 * Papi=blue / Mami=pink convention so the domain composables stay tidy.
 */
internal data class PlayerVisuals(
    val accent: Color,
    val soft: Color,
    val label: String,
    val displayName: String,
    val emoji: String,
)

@Composable
@ReadOnlyComposable
internal fun visualsOf(player: Player): PlayerVisuals {
    val colors = PapiPuntosTheme.colors
    return when (player) {
        Player.Papi -> PlayerVisuals(
            accent = colors.papi,
            soft = colors.papiSoft,
            label = "Papi Puntos",
            displayName = "Papi",
            emoji = "💙",
        )
        Player.Mami -> PlayerVisuals(
            accent = colors.mami,
            soft = colors.mamiSoft,
            label = "Mami Puntos",
            displayName = "Mami",
            emoji = "💗",
        )
    }
}
