package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jecsdev.papipuntos.designsystem.theme.DefaultPapiPuntosShapes
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/**
 * Full-width pill button filled with a horizontal gradient — the main CTA used
 * across the app (`bg-gradient-to-r` in the mockup). Domain-agnostic: the caller
 * provides the gradient and the optional colored drop shadow.
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    gradient: List<Color> = listOf(
        PapiPuntosTheme.colors.mami,
        PapiPuntosTheme.colors.papi,
    ),
    shadowColor: Color = gradient.first(),
    shape: RoundedCornerShape = DefaultPapiPuntosShapes.xxl,
) {
    Surface(
        onClick = onClick,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor,
            ),
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradient))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = text,
                color = Color.White,
                style = PapiPuntosTheme.typography.titleMedium,
            )
        }
    }
}
