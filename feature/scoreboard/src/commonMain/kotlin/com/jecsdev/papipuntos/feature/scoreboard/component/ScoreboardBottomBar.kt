package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/** The four destinations of the bottom navigation. */
enum class ScoreboardTab(val label: String, val icon: ImageVector) {
    Home("Inicio", PapiPuntosIcons.Home),
    History("Historial", PapiPuntosIcons.History),
    Rewards("Canjear", PapiPuntosIcons.Gift),
    Profile("Perfil", PapiPuntosIcons.Person),
}

/**
 * Floating bottom navigation bar (mockup: rounded card pinned with `inset-x-3
 * bottom-3`). The active tab is tinted with the Mami accent.
 */
@Composable
fun ScoreboardBottomBar(
    selected: ScoreboardTab,
    onSelect: (ScoreboardTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = PapiPuntosTheme.shapes.xxl
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), shape)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScoreboardTab.entries.forEach { tab ->
            BottomBarItem(
                tab = tab,
                selected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: ScoreboardTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color =
        if (selected) PapiPuntosTheme.colors.mami else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = color,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = tab.label,
            style = PapiPuntosTheme.typography.labelSmall,
            color = color,
        )
    }
}
