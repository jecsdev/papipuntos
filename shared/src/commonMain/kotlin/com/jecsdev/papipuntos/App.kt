package com.jecsdev.papipuntos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.data.db.platformAuthModule
import com.jecsdev.papipuntos.data.di.dataModule
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.domain.di.domainModule
import com.jecsdev.papipuntos.feature.addaction.AddActionScreen
import com.jecsdev.papipuntos.feature.addaction.di.addActionModule
import com.jecsdev.papipuntos.feature.approvals.ApprovalsScreen
import com.jecsdev.papipuntos.feature.approvals.di.approvalsModule
import com.jecsdev.papipuntos.feature.login.AuthViewModel
import com.jecsdev.papipuntos.feature.login.LoginScreen
import com.jecsdev.papipuntos.feature.login.ProfileSetupScreen
import com.jecsdev.papipuntos.feature.login.ProfilesScreen
import com.jecsdev.papipuntos.feature.login.di.loginModule
import com.jecsdev.papipuntos.feature.profile.PlansScreen
import com.jecsdev.papipuntos.feature.profile.ProfileScreen
import com.jecsdev.papipuntos.feature.rewards.RedeemScreen
import com.jecsdev.papipuntos.feature.rewards.RewardsScreen
import com.jecsdev.papipuntos.feature.rewards.model.Reward
import com.jecsdev.papipuntos.feature.scoreboard.HistoryScreen
import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardScreen
import com.jecsdev.papipuntos.feature.scoreboard.di.scoreboardModule
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

/** Screens the lightweight stand-in router can show once a profile is active. */
private enum class AppScreen { Scoreboard, AddAction, Approvals, History, Rewards, Redeem, Profile, Plans }

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(
                platformAuthModule,
                dataModule,
                domainModule,
                loginModule,
                scoreboardModule,
                addActionModule,
                approvalsModule,
            )
        },
    ) {
        PapiPuntosTheme { AppRoot() }
    }
}

/** Routes on [AuthState]: logged-out/setup/profile-picker screens, then the active app. */
@Composable
private fun AppRoot() {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    when (val state = authState) {
        AuthState.Loading -> SplashScreen()
        AuthState.LoggedOut -> LoginScreen()
        AuthState.NeedsSetup -> ProfileSetupScreen()
        is AuthState.ProfileSelection -> ProfilesScreen(profiles = state.profiles)
        is AuthState.Active -> ActiveApp(
            activePlayer = state.current.player,
            profiles = state.profiles,
            onLogout = { authViewModel.logOut() },
            onSwitchProfile = { authViewModel.requestProfileSwitch() },
        )
    }
}

/** Cold-start placeholder shown while [AuthState.Loading] resolves the persisted session. */
@Composable
private fun SplashScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Post-auth sub-router. Always starts on [AppScreen.Scoreboard] on every entry into
 * `Active` (e.g. after logging back in) — accepted for this stage, no screen memory yet.
 */
@Composable
private fun ActiveApp(
    activePlayer: Player,
    profiles: List<Profile>,
    onLogout: () -> Unit,
    onSwitchProfile: () -> Unit,
) {
    var screen by remember { mutableStateOf(AppScreen.Scoreboard) }
    // The reward picked on the Rewards list, carried into the Redeem screen.
    var selectedReward by remember { mutableStateOf<Reward?>(null) }
    when (screen) {
        AppScreen.Scoreboard -> ScoreboardScreen(
            activePlayer = activePlayer,
            profiles = profiles,
            onAddAction = { screen = AppScreen.AddAction },
            onSeeAllHistory = { screen = AppScreen.History },
            onOpenRewards = { screen = AppScreen.Rewards },
            onOpenProfile = { screen = AppScreen.Profile },
            onOpenApprovals = { screen = AppScreen.Approvals },
        )

        AppScreen.AddAction -> AddActionScreen(
            activePlayer = activePlayer,
            onBack = { screen = AppScreen.Scoreboard },
            // Picking an action returns to the board (logging is out of scope here).
            onActionPicked = { _, _ -> screen = AppScreen.Scoreboard },
        )

        AppScreen.Approvals -> ApprovalsScreen(
            activePlayer = activePlayer,
            onBack = { screen = AppScreen.Scoreboard },
        )

        AppScreen.History -> HistoryScreen(
            profiles = profiles,
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
            activePlayer = activePlayer,
            onBack = { screen = AppScreen.Scoreboard },
            onOpenPlans = { screen = AppScreen.Plans },
            onSwitchProfile = onSwitchProfile,
        )

        AppScreen.Plans -> PlansScreen(
            onBack = { screen = AppScreen.Profile },
            // Starting the trial would flip premium on; here it just returns.
            onStartTrial = { screen = AppScreen.Profile },
            onLogout = onLogout,
        )
    }
}
