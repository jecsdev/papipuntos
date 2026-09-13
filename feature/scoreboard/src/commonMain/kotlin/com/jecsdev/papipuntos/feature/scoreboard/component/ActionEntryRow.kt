package com.jecsdev.papipuntos.feature.scoreboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.component.ActionListItem
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry
import org.jetbrains.compose.resources.stringResource
import papipuntos.core.designsystem.generated.resources.Res
import papipuntos.core.designsystem.generated.resources.action_rejection_available
import papipuntos.core.designsystem.generated.resources.action_rejection_detail_close
import papipuntos.core.designsystem.generated.resources.action_rejection_detail_label
import papipuntos.core.designsystem.generated.resources.action_rejection_detail_title

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
    val rejectionReason = entry.rejectionReason?.takeIf(String::isNotBlank)
    var showingRejectionDetail by remember(entry.id, rejectionReason) { mutableStateOf(false) }
    ActionListItem(
        leadingEmoji = entry.emoji,
        leadingContainerColor = visuals.soft,
        title = entry.label,
        subtitle = listOfNotNull(
            "${entry.timestamp} · ${visuals.displayName}",
            rejectionReason?.let { stringResource(Res.string.action_rejection_available) },
        ).joinToString(" · "),
        modifier = modifier,
        onClick = rejectionReason?.let { { showingRejectionDetail = true } },
    ) {
        PointsBadge(
            text = "+${entry.points}",
            containerColor = visuals.accent,
            contentColor = Color.White,
        )
    }
    if (showingRejectionDetail && rejectionReason != null) {
        RejectionReasonDetailDialog(
            reason = rejectionReason,
            onDismiss = { showingRejectionDetail = false },
        )
    }
}

/** Shows the full rejection reason without truncating it inside a history row. */
@Composable
private fun RejectionReasonDetailDialog(
    reason: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = PapiPuntosTheme.colors.mami,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        shape = PapiPuntosTheme.shapes.xxxl,
        title = {
            Text(
                text = stringResource(Res.string.action_rejection_detail_title),
                style = PapiPuntosTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(Res.string.action_rejection_detail_label),
                    style = PapiPuntosTheme.typography.labelMedium,
                    color = PapiPuntosTheme.colors.mami,
                )
                Text(
                    text = reason,
                    style = PapiPuntosTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PapiPuntosTheme.colors.mami,
                    contentColor = Color.White,
                ),
            ) {
                Text(stringResource(Res.string.action_rejection_detail_close))
            }
        },
    )
}
