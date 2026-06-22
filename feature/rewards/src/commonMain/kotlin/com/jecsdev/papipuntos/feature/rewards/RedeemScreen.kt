package com.jecsdev.papipuntos.feature.rewards

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.component.DropdownSelector
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.component.PrimaryActionButton
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.rewards.model.Reward

/** Production entry point: owns the chosen reason and the success state. */
@Composable
fun RedeemScreen(
    reward: Reward,
    modifier: Modifier = Modifier,
    reasons: List<String> = RewardsSampleData.reasons,
    onBack: () -> Unit = {},
) {
    var reason by remember { mutableStateOf(reasons.first()) }
    var done by remember { mutableStateOf(false) }
    RedeemScreenContent(
        reward = reward,
        reasons = reasons,
        reason = reason,
        done = done,
        onReasonChange = { reason = it },
        onConfirm = { done = true },
        onBack = onBack,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun RedeemScreenContent(
    reward: Reward,
    reasons: List<String>,
    reason: String,
    done: Boolean,
    modifier: Modifier = Modifier,
    onReasonChange: (String) -> Unit = {},
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {},
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
        PapiPuntosTopBar(title = "Confirmar canje", onBack = onBack)

        Spacer(Modifier.height(12.dp))
        RewardHero(reward = reward)

        Spacer(Modifier.height(20.dp))
        Text(
            text = "¿Por qué quieres canjearla?",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(8.dp))
        DropdownSelector(
            selected = reason,
            options = reasons,
            onSelect = onReasonChange,
        )

        Spacer(Modifier.height(24.dp))
        PrimaryActionButton(
            text = "Canjear recompensa 💖",
            onClick = onConfirm,
            gradient = listOf(PapiPuntosTheme.colors.mami, MaterialTheme.colorScheme.primary),
        )

        AnimatedVisibility(visible = done) {
            SuccessBanner(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

/** The reward being redeemed, shown big on a soft gradient card. */
@Composable
private fun RewardHero(reward: Reward) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(
                Brush.linearGradient(
                    listOf(
                        PapiPuntosTheme.colors.mamiSoft,
                        MaterialTheme.colorScheme.background,
                        PapiPuntosTheme.colors.papiSoft,
                    ),
                ),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxxl)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(4.dp, PapiPuntosTheme.shapes.xxxl)
                .clip(PapiPuntosTheme.shapes.xxxl)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = reward.emoji, fontSize = 48.sp)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = reward.label,
            style = PapiPuntosTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))
        PointsBadge(
            text = "${reward.cost} pts",
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
        )
    }
}

/** Confirmation message shown after a successful redemption. */
@Composable
private fun SuccessBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxl)
            .background(PapiPuntosTheme.colors.mamiSoft)
            .border(1.dp, PapiPuntosTheme.colors.mami.copy(alpha = 0.3f), PapiPuntosTheme.shapes.xxl)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "✨ ¡Canje exitoso! Disfruta tu recompensa.",
            style = PapiPuntosTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = PapiPuntosTheme.colors.mami,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun RedeemScreenPreview() {
    PapiPuntosTheme {
        RedeemScreenContent(
            reward = RewardsSampleData.rewards.first(),
            reasons = RewardsSampleData.reasons,
            reason = RewardsSampleData.reasons.first(),
            done = false,
        )
    }
}
