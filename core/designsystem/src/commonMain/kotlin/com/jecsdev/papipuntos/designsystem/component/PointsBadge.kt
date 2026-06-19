package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Small pill badge for a points value (`rounded-full px-2.5 py-1` in the
 * mockup). Colors are caller-provided.
 */
@Composable
fun PointsBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = contentColor,
        style = PapiPuntosTheme.typography.labelMedium,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
