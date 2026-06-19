package com.jecsdev.papipuntos

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardScreen

@Composable
@Preview
fun App() {
    PapiPuntosTheme {
        ScoreboardScreen()
    }
}
