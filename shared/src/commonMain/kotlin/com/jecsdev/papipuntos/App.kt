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
import com.jecsdev.papipuntos.feature.profile.PlansScreen
import com.jecsdev.papipuntos.feature.profile.ProfileScreen
import com.jecsdev.papipuntos.feature.rewards.RedeemScreen
import com.jecsdev.papipuntos.feature.rewards.RewardsScreen
import com.jecsdev.papipuntos.feature.rewards.model.Reward
import com.jecsdev.papipuntos.feature.scoreboard.HistoryScreen
import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardScreen

/** Screens the lightweight stand-in router can show. Real navigation comes later. */
private enum class AppScreen { Login, Scoreboard, AddAction, History, Rewards, Redeem, Profile, Plans }

@Composable
@Preview
fun App() {
    PapiPuntosTheme {
        var screen by remember { mutableStateOf(AppScreen.Login) }
        // The reward picked on the Rewards list, carried into the Redeem screen.
        var selectedReward by remember { mutableStateOf<Reward?>(null) }
        when (screen) {
            AppScreen.Login -> LoginScreen(
                onAuthenticated = { screen = AppScreen.Scoreboard },
            )

            AppScreen.Scoreboard -> ScoreboardScreen(
                onAddAction = { screen = AppScreen.AddAction },
                onSeeAllHistory = { screen = AppScreen.History },
                onOpenRewards = { screen = AppScreen.Rewards },
                onOpenProfile = { screen = AppScreen.Profile },
            )

            AppScreen.AddAction -> AddActionScreen(
                onBack = { screen = AppScreen.Scoreboard },
                // Picking an action returns to the board (logging is out of scope here).
                onActionPicked = { _, _ -> screen = AppScreen.Scoreboard },
            )

            AppScreen.History -> HistoryScreen(
                onBack = { screen = AppScreen.Scoreboard },
            )

            AppScreen.Rewards -> RewardsScreen(
                onBack = { screen = AppScreen.Scoreboard },
                onRewardSelected = {
                    selectedReward = it
                    screen = AppScreen.Redeem
                },
            )

            AppScreen.Redeem -> selectedReward?.let { reward ->
                RedeemScreen(
                    reward = reward,
                    onBack = { screen = AppScreen.Rewards },
                )
            }

            AppScreen.Profile -> ProfileScreen(
                onBack = { screen = AppScreen.Scoreboard },
                onOpenPlans = { screen = AppScreen.Plans },
            )

            AppScreen.Plans -> PlansScreen(
                onBack = { screen = AppScreen.Profile },
                // Starting the trial would flip premium on; here it just returns.
                onStartTrial = { screen = AppScreen.Profile },
                onLogout = { screen = AppScreen.Login },
            )
        }
    }
}
