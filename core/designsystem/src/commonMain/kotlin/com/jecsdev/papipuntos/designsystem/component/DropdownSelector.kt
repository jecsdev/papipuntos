package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Generic single-choice dropdown: a `border bg-card rounded-2xl` trigger showing
 * the current value plus a chevron that flips when open, and an expandable card
 * listing the options with a check on the selected one. Owns its open/closed
 * state; the caller only supplies the value and the choices.
 */
@Composable
fun DropdownSelector(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f)
    val shape = PapiPuntosTheme.shapes.xxl

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected,
                style = PapiPuntosTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = PapiPuntosIcons.ChevronDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(chevronRotation),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape),
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(option)
                                expanded = false
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = option,
                            style = PapiPuntosTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        if (option == selected) {
                            Icon(
                                imageVector = PapiPuntosIcons.Check,
                                contentDescription = null,
                                tint = PapiPuntosTheme.colors.mami,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
