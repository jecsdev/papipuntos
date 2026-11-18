package com.jecsdev.papipuntos.feature.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.designsystem.component.DropdownSelector
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.rewards.model.Reward
import com.jecsdev.papipuntos.model.Player
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Production entry point: owns the active reward filter. */
@Composable
fun RewardsScreen(
    activePlayer: Player,
    modifier: Modifier = Modifier,
    filters: List<String> = RewardUiOptions.filters,
    onBack: () -> Unit = {},
    onRewardSelected: (Reward) -> Unit = {},
    viewModel: RewardsViewModel = koinViewModel(
        key = "rewards-${activePlayer.name}",
        parameters = { parametersOf(activePlayer) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(filters.first()) }
    var showCreate by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🎁") }
    RewardsScreenContent(
        rewards = state.rewards,
        balance = state.balance,
        filters = filters,
        filter = filter,
        onFilterChange = { filter = it },
        onBack = onBack,
        onRewardSelected = onRewardSelected,
        onCreateReward = { showCreate = true },
        errorMessage = state.errorMessage,
        modifier = modifier,
    )
    if (showCreate) {
        AlertDialog(
            onDismissRequest = { if (!isSaving) showCreate = false },
            title = { Text("Nueva recompensa") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(label, { label = it }, label = { Text("Recompensa") }, singleLine = true)
                    OutlinedTextField(
                        cost,
                        { cost = it.filter(Char::isDigit) },
                        label = { Text("Costo en puntos") },
                        singleLine = true,
                    )
                    OutlinedTextField(emoji, { emoji = it }, label = { Text("Emoji") }, singleLine = true)
                    state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createReward(label, cost, emoji) {
                            showCreate = false
                            label = ""
                            cost = ""
                        }
                    },
                    enabled = !isSaving,
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = { TextButton(onClick = { showCreate = false }, enabled = !isSaving) { Text("Cancelar") } },
        )
    }
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun RewardsScreenContent(
    rewards: List<Reward>,
    balance: Int,
    filters: List<String>,
    filter: String,
    modifier: Modifier = Modifier,
    onFilterChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onRewardSelected: (Reward) -> Unit = {},
    onCreateReward: () -> Unit = {},
    errorMessage: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        PapiPuntosTopBar(title = "Canjear puntos", onBack = onBack)

        TextButton(onClick = onCreateReward, modifier = Modifier.fillMaxWidth()) { Text("+ Agregar recompensa") }
        errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "¿Por cuál recompensa quieres canjear tus puntos?",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(8.dp))
        PointsBadge(
            text = "$balance pts",
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
        )

        Spacer(Modifier.height(8.dp))
        DropdownSelector(
            selected = filter,
            options = filters,
            onSelect = onFilterChange,
        )

        Spacer(Modifier.height(20.dp))
        val visibleRewards = rewards.filter { reward -> reward.matches(filter) }
        if (visibleRewards.isEmpty()) {
            Text("No hay recompensas con este filtro", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                visibleRewards.forEach { reward ->
                    RewardRow(reward = reward, balance = balance, onClick = { onRewardSelected(reward) })
                }
            }
        }
    }
}

private fun Reward.matches(filter: String): Boolean = when (filter) {
    "Hasta 400 pts" -> cost <= 400
    "Entre 400 y 600 pts" -> cost in 400..600
    "Premium 600+" -> cost >= 600
    else -> true
}

/** A redeemable reward row: gradient tile + label + availability hint + cost pill. */
@Composable
private fun RewardRow(
    reward: Reward,
    balance: Int,
    onClick: () -> Unit,
) {
    val shape = PapiPuntosTheme.shapes.xxl
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(PapiPuntosTheme.colors.mamiSoft, PapiPuntosTheme.colors.papiSoft),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = reward.emoji, fontSize = 24.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = reward.label,
                style = PapiPuntosTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (balance >= reward.cost) "Disponible para canjear" else "Te faltan ${reward.cost - balance} pts",
                style = PapiPuntosTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        PointsBadge(
            text = "${reward.cost} pts",
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Preview
@Composable
private fun RewardsScreenPreview() {
    PapiPuntosTheme {
        RewardsScreenContent(
            rewards = RewardsSampleData.rewards,
            balance = 730,
            filters = RewardUiOptions.filters,
            filter = RewardUiOptions.filters.first(),
        )
    }
}
