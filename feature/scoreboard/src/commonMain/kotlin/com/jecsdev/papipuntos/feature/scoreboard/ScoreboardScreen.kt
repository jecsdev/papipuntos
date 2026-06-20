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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jecsdev.papipuntos.designsystem.component.PrimaryActionButton
import com.jecsdev.papipuntos.designsystem.component.SectionHeader
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.component.ActionEntryRow
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreCard
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardBottomBar
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardHeader
import com.jecsdev.papipuntos.feature.scoreboard.component.ScoreboardTab
import com.jecsdev.papipuntos.model.Player
import androidx.compose.ui.tooling.preview.Preview

/** Production entry point: pulls state from the ViewModel and drives [ScoreboardScreenContent]. */
@Composable
fun ScoreboardScreen(
    viewModel: ScoreboardViewModel = viewModel { ScoreboardViewModel() },
    onAddAction: () -> Unit = {},
    onSeeAllHistory: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(ScoreboardTab.Home) }
    ScoreboardScreenContent(
        state = viewModel.uiState,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onAddAction = onAddAction,
        onSeeAllHistory = onSeeAllHistory,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun ScoreboardScreenContent(
    state: ScoreboardUiState,
    selectedTab: ScoreboardTab,
    modifier: Modifier = Modifier,
    onTabSelected: (ScoreboardTab) -> Unit = {},
    onAddAction: () -> Unit = {},
    onSeeAllHistory: () -> Unit = {},
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
                coupleNames = state.coupleNames,
                papiAvatar = state.papiAvatar,
                mamiAvatar = state.mamiAvatar,
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

            Spacer(Modifier.height(24.dp))

            PrimaryActionButton(
                text = "Agregar acción",
                onClick = onAddAction,
                leadingIcon = PapiPuntosIcons.Add,
            )

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

@Preview
@Composable
private fun ScoreboardScreenPreview() {
    PapiPuntosTheme {
        ScoreboardScreenContent(
            state = ScoreboardSampleData.state,
            selectedTab = ScoreboardTab.Home,
        )
    }
}
