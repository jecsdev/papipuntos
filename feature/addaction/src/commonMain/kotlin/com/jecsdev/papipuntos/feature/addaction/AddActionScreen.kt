package com.jecsdev.papipuntos.feature.addaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.jecsdev.papipuntos.designsystem.component.ActionListItem
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTextField
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.component.SegmentedToggle
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.addaction.model.SuggestedAction
import com.jecsdev.papipuntos.model.Player

/** Production entry point: owns the selected profile and search query. */
@Composable
fun AddActionScreen(
    modifier: Modifier = Modifier,
    suggestions: List<SuggestedAction> = AddActionSampleData.suggestions,
    onBack: () -> Unit = {},
    onActionPicked: (Player, SuggestedAction) -> Unit = { _, _ -> },
) {
    var target by remember { mutableStateOf(Player.Papi) }
    var query by remember { mutableStateOf("") }
    AddActionScreenContent(
        suggestions = suggestions,
        target = target,
        query = query,
        onTargetChange = { target = it },
        onQueryChange = { query = it },
        onBack = onBack,
        onActionPicked = { onActionPicked(target, it) },
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun AddActionScreenContent(
    suggestions: List<SuggestedAction>,
    target: Player,
    query: String,
    modifier: Modifier = Modifier,
    onTargetChange: (Player) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onActionPicked: (SuggestedAction) -> Unit = {},
) {
    val filtered = suggestions.filter { it.label.contains(query, ignoreCase = true) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        PapiPuntosTopBar(title = "Agregar acción", onBack = onBack)

        Spacer(Modifier.height(12.dp))
        SegmentedToggle(
            options = listOf("💙 Para Papi", "💗 Para Mami"),
            selectedIndex = target.ordinal,
            onSelect = { onTargetChange(Player.entries[it]) },
            activeContainerColor = when (target) {
                Player.Papi -> PapiPuntosTheme.colors.papi
                Player.Mami -> PapiPuntosTheme.colors.mami
            },
            activeContentColor = Color.White,
        )

        Spacer(Modifier.height(16.dp))
        PapiPuntosTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Buscar acción...",
            leadingIcon = PapiPuntosIcons.Search,
        )

        Spacer(Modifier.height(20.dp))
        Text(
            text = "SUGERIDAS",
            style = PapiPuntosTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.08.em,
        )

        Spacer(Modifier.height(8.dp))
        if (filtered.isEmpty()) {
            EmptyResults()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { action ->
                    ActionListItem(
                        leadingEmoji = action.emoji,
                        leadingContainerColor = MaterialTheme.colorScheme.tertiary,
                        title = action.label,
                        onClick = { onActionPicked(action) },
                    ) {
                        PointsBadge(
                            text = "+${action.points}",
                            containerColor = PapiPuntosTheme.colors.mamiSoft,
                            contentColor = PapiPuntosTheme.colors.mami,
                        )
                    }
                }
            }
        }
    }
}

/** Placeholder card shown when the query matches nothing. */
@Composable
private fun EmptyResults() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, PapiPuntosTheme.shapes.xxl)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sin resultados",
            style = PapiPuntosTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun AddActionScreenPreview() {
    PapiPuntosTheme {
        AddActionScreenContent(
            suggestions = AddActionSampleData.suggestions,
            target = Player.Papi,
            query = "",
        )
    }
}
