package com.jecsdev.papipuntos.feature.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.designsystem.component.SectionHeader
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.component.ActionEntryRow
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreCard
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardBottomBar
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardHeader
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardTab
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Production entry point: pulls state from the ViewModel and drives [ScoreboardScreenContent]. */
@Composable
fun ScoreboardScreen(
    activePlayer: Player,
    profiles: List<Profile>,
    viewModel: ScoreboardViewModel = koinViewModel(
        key = "scoreboard-${activePlayer.name}",
        parameters = { parametersOf(activePlayer, profiles) },
    ),
    onAddAction: () -> Unit = {},
    onSeeAllHistory: () -> Unit = {},
    onOpenRewards: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenApprovals: () -> Unit = {},
    onSwitchProfile: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ScoreboardScreenContent(
        state = state,
        activePlayer = activePlayer,
        selectedTab = ScoreboardTab.Home,
        // Tabs that own a screen navigate away; Home just stays put.
        onTabSelected = { tab ->
            when (tab) {
                ScoreboardTab.Home -> Unit
                ScoreboardTab.History -> onSeeAllHistory()
                ScoreboardTab.Actions -> onAddAction()
                ScoreboardTab.Rewards -> onOpenRewards()
                ScoreboardTab.Profile -> onOpenProfile()
            }
        },
        onSeeAllHistory = onSeeAllHistory,
        onOpenApprovals = onOpenApprovals,
        onSwitchProfile = onSwitchProfile,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun ScoreboardScreenContent(
    state: ScoreboardUiState,
    activePlayer: Player,
    selectedTab: ScoreboardTab,
    modifier: Modifier = Modifier,
    onTabSelected: (ScoreboardTab) -> Unit = {},
    onSeeAllHistory: () -> Unit = {},
    onOpenApprovals: () -> Unit = {},
    onSwitchProfile: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp),
        ) {
            ScoreboardHeader(
                activeName = if (activePlayer == Player.Papi) state.papiName else state.mamiName,
                activeAvatar = if (activePlayer == Player.Papi) state.papiAvatar else state.mamiAvatar,
                activePlayer = activePlayer,
                onSwitchProfile = onSwitchProfile,
            )

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScoreCard(
                    player = Player.Papi,
                    points = state.papiPoints,
                    goalPoints = state.goalPoints,
                    modifier = Modifier.weight(1f),
                )
                ScoreCard(
                    player = Player.Mami,
                    points = state.mamiPoints,
                    goalPoints = state.goalPoints,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))

            SectionHeader(
                title = "Últimas acciones",
                actionText = "Ver todo",
                actionColor = PapiPuntosTheme.colors.mami,
                onActionClick = onSeeAllHistory,
            )

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.recentActions.take(6).forEach { entry ->
                    ActionEntryRow(entry = entry)
                }
            }

            if (state.pendingApprovalCount > 0) {
                Spacer(Modifier.height(24.dp))
                SectionHeader(
                    title = "Solicitudes pendientes",
                    actionText = "Revisar (${state.pendingApprovalCount})",
                    actionColor = PapiPuntosTheme.colors.mami,
                    onActionClick = onOpenApprovals,
                )
            }

            // Leaves room for the floating bottom bar (mockup `pb-24`).
            Spacer(Modifier.height(96.dp))
        }

        ScoreboardBottomBar(
            selected = selectedTab,
            onSelect = onTabSelected,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(12.dp),
        )
    }
}
