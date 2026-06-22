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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.component.DropdownSelector
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.rewards.model.Reward

/** Production entry point: owns the active reward filter. */
@Composable
fun RewardsScreen(
    modifier: Modifier = Modifier,
    rewards: List<Reward> = RewardsSampleData.rewards,
    filters: List<String> = RewardsSampleData.filters,
    onBack: () -> Unit = {},
    onRewardSelected: (Reward) -> Unit = {},
) {
    var filter by remember { mutableStateOf(filters.first()) }
    RewardsScreenContent(
        rewards = rewards,
        filters = filters,
        filter = filter,
        onFilterChange = { filter = it },
        onBack = onBack,
        onRewardSelected = onRewardSelected,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun RewardsScreenContent(
    rewards: List<Reward>,
    filters: List<String>,
    filter: String,
    modifier: Modifier = Modifier,
    onFilterChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onRewardSelected: (Reward) -> Unit = {},
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

        Spacer(Modifier.height(12.dp))
        Text(
            text = "¿Por cuál recompensa quieres canjear tus puntos?",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(8.dp))
        DropdownSelector(
            selected = filter,
            options = filters,
            onSelect = onFilterChange,
        )

        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rewards.forEach { reward ->
                RewardRow(reward = reward, onClick = { onRewardSelected(reward) })
            }
        }
    }
}

/** A redeemable reward row: gradient tile + label + availability hint + cost pill. */
@Composable
private fun RewardRow(
    reward: Reward,
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
                text = "Disponible para canjear",
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
            filters = RewardsSampleData.filters,
            filter = RewardsSampleData.filters.first(),
        )
    }
}
