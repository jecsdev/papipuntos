package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.component.ProfileAvatar
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.model.Player

/**
 * Top of the scoreboard: the active profile's greeting and avatar, with the
 * couple context still represented by the screen title and score cards.
 */
@Composable
fun ScoreboardHeader(
    activeName: String,
    activeAvatar: String,
    activePlayer: Player,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "¡Hola, $activeName!",
                style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Marcador de la pareja",
                style = PapiPuntosTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        ProfileAvatar(
            emoji = activeAvatar,
            ringColor = when (activePlayer) {
                Player.Papi -> PapiPuntosTheme.colors.papi
                Player.Mami -> PapiPuntosTheme.colors.mami
            },
        )
    }
}
