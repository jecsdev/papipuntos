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

/**
 * Top of the scoreboard: greeting + screen title on the left, the couple's
 * overlapping avatars on the right.
 */
@Composable
fun ScoreboardHeader(
    coupleNames: String,
    papiAvatar: String,
    mamiAvatar: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "¡Hola, $coupleNames! 💕",
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

        // Overlapping avatars (mockup: `flex -space-x-2`), papi drawn over mami.
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            ProfileAvatar(emoji = mamiAvatar, ringColor = PapiPuntosTheme.colors.mami)
            ProfileAvatar(emoji = papiAvatar, ringColor = PapiPuntosTheme.colors.papi)
        }
    }
}
