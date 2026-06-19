package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Row with a bold section title and an optional trailing text action
 * (e.g. "Ver todo"). Action color is caller-provided so it can match the
 * active profile accent.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    actionColor: Color = MaterialTheme.colorScheme.primary,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = PapiPuntosTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (actionText != null) {
            Text(
                text = actionText,
                style = PapiPuntosTheme.typography.labelMedium,
                color = actionColor,
                modifier = if (onActionClick != null) Modifier.clickable(onClick = onActionClick) else Modifier,
            )
        }
    }
}
