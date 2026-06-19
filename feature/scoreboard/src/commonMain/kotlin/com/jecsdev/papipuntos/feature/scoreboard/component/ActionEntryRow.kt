package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jecsdev.papipuntos.designsystem.component.ActionListItem
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry

/**
 * A logged action row, built on the generic [ActionListItem]. Colors the leading
 * tile and the points badge by the action's [Player], and appends the author to
 * the timestamp.
 */
@Composable
fun ActionEntryRow(
    entry: ActionEntry,
    modifier: Modifier = Modifier,
) {
    val visuals = visualsOf(entry.player)
    ActionListItem(
        leadingEmoji = entry.emoji,
        leadingContainerColor = visuals.soft,
        title = entry.label,
        subtitle = "${entry.timestamp} · ${visuals.displayName}",
        modifier = modifier,
    ) {
        PointsBadge(
            text = "+${entry.points}",
            containerColor = visuals.accent,
            contentColor = Color.White,
        )
    }
}
