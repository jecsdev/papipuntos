package com.jecsdev.papipuntos.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box

/**
 * Circular avatar holding an emoji on a white disc with a colored ring — the
 * `rounded-full bg-white ring-2` pattern from the mockup. Ring color is supplied
 * by the caller so it stays domain-agnostic.
 */
@Composable
fun ProfileAvatar(
    emoji: String,
    ringColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    ringWidth: Dp = 2.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(ringWidth, ringColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = (size.value * 0.5f).sp)
    }
}
