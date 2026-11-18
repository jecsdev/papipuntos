package com.jecsdev.papipuntos

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.jecsdev.papipuntos.feature.profile.di.profileModule
import com.jecsdev.papipuntos.feature.rewards.RedeemScreen
import com.jecsdev.papipuntos.feature.rewards.RewardsScreen
import com.jecsdev.papipuntos.feature.rewards.di.rewardsModule
import com.jecsdev.papipuntos.feature.rewards.model.Reward
import com.jecsdev.papipuntos.feature.scoreboard.HistoryScreen
import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardScreen
import com.jecsdev.papipuntos.feature.scoreboard.di.scoreboardModule
import com.jecsdev.papipuntos.model.AuthState
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

/** Stable routes for the app navigation graph. */
private object AppRoute {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val PROFILE_SETUP = "profile-setup"
    const val PROFILE_SELECTION = "profile-selection"
    const val SCOREBOARD = "scoreboard"
    const val ADD_ACTION = "add-action"
    const val APPROVALS = "approvals"
    const val HISTORY = "history"
    const val REWARDS = "rewards"
    const val REDEEM = "redeem"
    const val PROFILE = "profile"
    const val PLANS = "plans"

    val ACTIVE_ROUTES = setOf(
        SCOREBOARD,
        ADD_ACTION,
        APPROVALS,
        HISTORY,
        REWARDS,
        REDEEM,
        PROFILE,
        PLANS,
    )
}

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
                rewardsModule,
                profileModule,
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
    val bootstrapError by authViewModel.bootstrapError.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var selectedReward by remember { mutableStateOf<Reward?>(null) }

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Loading -> navController.replaceWith(AppRoute.SPLASH)
            AuthState.LoggedOut -> navController.replaceWith(AppRoute.LOGIN)
            AuthState.NeedsSetup -> navController.replaceWith(AppRoute.PROFILE_SETUP)
            is AuthState.ProfileSelection -> navController.replaceWith(AppRoute.PROFILE_SELECTION)
            is AuthState.Active -> if (navController.currentDestination?.route !in AppRoute.ACTIVE_ROUTES) {
                navController.replaceWith(AppRoute.SCOREBOARD)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(),
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(),
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(),
            )
        },
    ) {
        composable(AppRoute.SPLASH) { SplashScreen(bootstrapError, authViewModel::retryBootstrap) }
        composable(AppRoute.LOGIN) { LoginScreen() }
        composable(AppRoute.PROFILE_SETUP) { ProfileSetupScreen() }
        composable(AppRoute.PROFILE_SELECTION) {
            (authState as? AuthState.ProfileSelection)?.let { state ->
                ProfilesScreen(profiles = state.profiles)
            } ?: SplashScreen()
        }
        composable(AppRoute.SCOREBOARD) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                ScoreboardScreen(
                    activePlayer = state.current.player,
                    profiles = state.profiles,
                    onAddAction = { navController.navigateFrom(entry, AppRoute.ADD_ACTION) },
                    onSeeAllHistory = { navController.navigateFrom(entry, AppRoute.HISTORY) },
                    onOpenRewards = { navController.navigateFrom(entry, AppRoute.REWARDS) },
                    onOpenProfile = { navController.navigateFrom(entry, AppRoute.PROFILE) },
                    onOpenApprovals = { navController.navigateFrom(entry, AppRoute.APPROVALS) },
                    onSwitchProfile = { authViewModel.requestProfileSwitch() },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.ADD_ACTION) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                AddActionScreen(
                    activePlayer = state.current.player,
                    onBack = { navController.popFrom(entry) },
                    onActionPicked = { _, _ -> navController.popFrom(entry) },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.APPROVALS) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                ApprovalsScreen(
                    activePlayer = state.current.player,
                    onBack = { navController.popFrom(entry) },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.HISTORY) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                HistoryScreen(
                    activePlayer = state.current.player,
                    profiles = state.profiles,
                    onBack = { navController.popFrom(entry) },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.REWARDS) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                RewardsScreen(
                    activePlayer = state.current.player,
                    onBack = { navController.popFrom(entry) },
                    onRewardSelected = { reward ->
                        if (navController.currentBackStackEntry === entry) {
                            selectedReward = reward
                            navController.navigateFrom(entry, AppRoute.REDEEM)
                        }
                    },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.REDEEM) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                selectedReward?.let { reward ->
                    RedeemScreen(
                        reward = reward,
                        activePlayer = state.current.player,
                        onBack = { navController.popFrom(entry) },
                    )
                } ?: run {
                    LaunchedEffect(entry) {
                        if (navController.currentBackStackEntry === entry &&
                            !navController.popBackStack(AppRoute.REWARDS, inclusive = false)
                        ) {
                            navController.navigate(AppRoute.REWARDS) {
                                popUpTo(AppRoute.REDEEM) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                    SplashScreen()
                }
            } ?: SplashScreen()
        }
        composable(AppRoute.PROFILE) { entry ->
            (authState as? AuthState.Active)?.let { state ->
                ProfileScreen(
                    activePlayer = state.current.player,
                    profiles = state.profiles,
                    onBack = { navController.popFrom(entry) },
                    onOpenPlans = { navController.navigateFrom(entry, AppRoute.PLANS) },
                    onSwitchProfile = { authViewModel.requestProfileSwitch() },
                )
            } ?: SplashScreen()
        }
        composable(AppRoute.PLANS) { entry ->
            PlansScreen(
                onBack = { navController.popFrom(entry) },
                onStartTrial = { navController.popFrom(entry) },
                onLogout = { authViewModel.logOut() },
            )
        }
    }
}

// During a slide transition the outgoing screen can still have callbacks in flight.
private fun NavHostController.navigateFrom(entry: NavBackStackEntry, route: String) {
    if (currentBackStackEntry === entry) {
        navigate(route) { launchSingleTop = true }
    }
}

private fun NavHostController.popFrom(entry: NavBackStackEntry) {
    if (currentBackStackEntry === entry) {
        popBackStack()
    }
}

private fun NavHostController.replaceWith(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Cold-start placeholder shown while [AuthState.Loading] resolves the persisted session. */
@Composable
private fun SplashScreen(errorMessage: String? = null, onRetry: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (errorMessage == null) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No se pudo abrir la app: $errorMessage")
                    TextButton(onClick = onRetry) { Text("Reintentar") }
                }
            }
        }
    }
}
