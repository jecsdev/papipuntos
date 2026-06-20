package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Generic segmented control: a `bg-muted` track holding equal-width options where
 * the selected one is lifted onto a filled pill (`bg-white shadow-sm` in the
 * mockup). The active colors are caller-provided so the same control serves the
 * neutral login/signup switch and the profile-tinted Papi/Mami switches.
 */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeContainerColor: Color = Color.White,
    activeContentColor: Color = MaterialTheme.colorScheme.onSurface,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val trackShape = PapiPuntosTheme.shapes.xxl
    val pillShape = PapiPuntosTheme.shapes.xl
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(trackShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, label ->
            val active = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(if (active) Modifier.shadow(2.dp, pillShape) else Modifier)
                    .clip(pillShape)
                    .background(if (active) activeContainerColor else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = PapiPuntosTheme.typography.labelMedium,
                    color = if (active) activeContentColor else inactiveContentColor,
                )
            }
        }
    }
}
