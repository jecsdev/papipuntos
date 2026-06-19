package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jecsdev.papipuntos.designsystem.component.CounterCard
import com.jecsdev.papipuntos.feature.scoreboard.model.Player

/**
 * Per-profile points card (Papi blue / Mami pink), built on the generic
 * [CounterCard]. Adds the domain knowledge: which color, label and emoji each
 * player gets, and how points map to the goal progress.
 */
@Composable
fun ScoreCard(
    player: Player,
    points: Int,
    goalPoints: Int,
    modifier: Modifier = Modifier,
) {
    val visuals = visualsOf(player)
    CounterCard(
        label = visuals.label,
        value = points.toString(),
        unit = "pts",
        progress = if (goalPoints > 0) points.toFloat() / goalPoints else 0f,
        accentColor = visuals.accent,
        containerColor = visuals.soft,
        leadingEmoji = visuals.emoji,
        modifier = modifier,
    )
}
