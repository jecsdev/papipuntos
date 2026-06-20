package com.jecsdev.papipuntos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.addaction.AddActionScreen
import com.jecsdev.papipuntos.feature.login.LoginScreen
import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardScreen

/** Screens the lightweight stand-in router can show. Real navigation comes later. */
private enum class AppScreen { Login, Scoreboard, AddAction }

@Composable
@Preview
fun App() {
    PapiPuntosTheme {
        var screen by remember { mutableStateOf(AppScreen.Login) }
        when (screen) {
            AppScreen.Login -> LoginScreen(
                onAuthenticated = { screen = AppScreen.Scoreboard },
            )

            AppScreen.Scoreboard -> ScoreboardScreen(
                onAddAction = { screen = AppScreen.AddAction },
            )

            AppScreen.AddAction -> AddActionScreen(
                onBack = { screen = AppScreen.Scoreboard },
                // Picking an action returns to the board (logging is out of scope here).
                onActionPicked = { _, _ -> screen = AppScreen.Scoreboard },
            )
        }
    }
}
