package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Horizontal rule with a centered caption — the mockup's "o continúa con"
 * separator (`h-px bg-border` lines flanking uppercase, letter-spaced muted text).
 */
@Composable
fun LabeledDivider(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Line(Modifier.weight(1f))
        Text(
            text = text.uppercase(),
            style = PapiPuntosTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.08.em,
        )
        Line(Modifier.weight(1f))
    }
}

@Composable
private fun Line(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline),
    )
}
