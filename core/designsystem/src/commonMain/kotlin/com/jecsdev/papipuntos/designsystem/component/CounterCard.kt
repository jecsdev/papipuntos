package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Generic counter card: a soft-tinted rounded card with a labelled header, a big
 * value with its unit, and a thin progress bar. Knows nothing about the domain —
 * the caller passes the accent color, container color, copy and progress.
 *
 * Mirrors the `rounded-3xl` score cards from the mockup.
 */
@Composable
fun CounterCard(
    label: String,
    value: String,
    unit: String,
    progress: Float,
    accentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    leadingEmoji: String? = null,
) {
    val shape = PapiPuntosTheme.shapes.xxxl
    Column(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), shape)
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingEmoji != null) {
                Text(text = leadingEmoji, fontSize = 18.sp)
            }
            Text(
                text = label.uppercase(),
                color = accentColor,
                style = PapiPuntosTheme.typography.labelMedium.copy(letterSpacing = 0.08.em),
            )
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = value,
                style = PapiPuntosTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = unit,
                modifier = Modifier.padding(bottom = 4.dp),
                style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LinearProgress(
            fraction = progress,
            trackColor = Color.White.copy(alpha = 0.7f),
            barColor = accentColor,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/**
 * Minimal rounded progress bar (height 6dp) matching the mockup's `h-1.5`
 * track. Kept private because [CounterCard] is its only consumer for now.
 */
@Composable
private fun LinearProgress(
    fraction: Float,
    trackColor: Color,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    val safe = fraction.coerceIn(0f, 1f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .progressSemantics(safe),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(safe)
                .height(6.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(barColor),
        ) {}
    }
}
