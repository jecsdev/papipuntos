package com.jecsdev.papipuntos.feature.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.SegmentedToggle
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.scoreboard.component.ActionEntryRow
import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** The three filter tabs over the logged actions: everyone, only Papi, only Mami. */
enum class HistoryFilter(
    val label: String,
    val player: Player?,
) {
    All("Todos", null),
    Papi("💙 Papi", Player.Papi),
    Mami("💗 Mami", Player.Mami),
}

/** Production entry point: owns the active filter. */
@Composable
fun HistoryScreen(
    activePlayer: Player,
    profiles: List<Profile>,
    modifier: Modifier = Modifier,
    viewModel: ScoreboardViewModel = koinViewModel(
        key = "scoreboard-${activePlayer.name}",
        parameters = { parametersOf(activePlayer, profiles) },
    ),
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(HistoryFilter.All) }
    HistoryScreenContent(
        actions = state.recentActions,
        filter = filter,
        onFilterChange = { filter = it },
        onBack = onBack,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun HistoryScreenContent(
    actions: List<ActionEntry>,
    filter: HistoryFilter,
    modifier: Modifier = Modifier,
    onFilterChange: (HistoryFilter) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val visible = actions.filter { filter.player == null || it.player == filter.player }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { PapiPuntosTopBar(title = "Historial", onBack = onBack) }

        item { Spacer(Modifier.height(4.dp)) }
        item {
            SegmentedToggle(
                options = HistoryFilter.entries.map { it.label },
                selectedIndex = filter.ordinal,
                onSelect = { onFilterChange(HistoryFilter.entries[it]) },
                activeContainerColor = when (filter) {
                    HistoryFilter.All -> MaterialTheme.colorScheme.onBackground
                    HistoryFilter.Papi -> PapiPuntosTheme.colors.papi
                    HistoryFilter.Mami -> PapiPuntosTheme.colors.mami
                },
                activeContentColor = when (filter) {
                    HistoryFilter.All -> MaterialTheme.colorScheme.background
                    else -> Color.White
                },
            )
        }

        item { Spacer(Modifier.height(12.dp)) }
        item {
            Text(
                text = "TODAS LAS ACCIONES",
                style = PapiPuntosTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.08.em,
            )
        }

        items(visible, key = { it.id }) { entry ->
            ActionEntryRow(entry = entry)
        }
    }
}
