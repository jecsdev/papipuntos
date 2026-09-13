package com.jecsdev.papipuntos.feature.approvals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.designsystem.component.ActionListItem
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PointsBadge
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.domain.action.ActionDecision
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.Player
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.stringResource
import papipuntos.core.designsystem.generated.resources.Res
import papipuntos.core.designsystem.generated.resources.approvals_approve
import papipuntos.core.designsystem.generated.resources.approvals_description
import papipuntos.core.designsystem.generated.resources.approvals_empty
import papipuntos.core.designsystem.generated.resources.approvals_reject
import papipuntos.core.designsystem.generated.resources.approvals_request_subtitle
import papipuntos.core.designsystem.generated.resources.approvals_saving
import papipuntos.core.designsystem.generated.resources.approvals_title
import papipuntos.core.designsystem.generated.resources.points_badge

/** Production entry point for requests that the active profile must review. */
@Composable
fun ApprovalsScreen(
    activePlayer: Player,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: ApprovalsViewModel = koinViewModel(parameters = { parametersOf(activePlayer) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ApprovalsScreenContent(
        actions = state.actions,
        resolvingActionId = state.resolvingActionId,
        errorMessage = state.errorMessage,
        onApprove = { action -> viewModel.decide(action, ActionDecision.Approve) },
        onReject = { action -> viewModel.decide(action, ActionDecision.Reject) },
        onBack = onBack,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with the review actions exposed as callbacks. */
@Composable
fun ApprovalsScreenContent(
    actions: List<Action>,
    resolvingActionId: String?,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onApprove: (Action) -> Unit = {},
    onReject: (Action) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PapiPuntosTheme.colors.canvas)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PapiPuntosTopBar(title = stringResource(Res.string.approvals_title), onBack = onBack)
        Text(
            text = stringResource(Res.string.approvals_description),
            style = PapiPuntosTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = PapiPuntosTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
            )
        }
        if (actions.isEmpty()) {
            EmptyApprovals()
        } else {
            actions.forEach { action ->
                PendingApprovalCard(
                    action = action,
                    isResolving = resolvingActionId == action.id,
                    onApprove = { onApprove(action) },
                    onReject = { onReject(action) },
                )
            }
        }
    }
}

@Composable
private fun PendingApprovalCard(
    action: Action,
    isResolving: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    val accent = when (action.beneficiary) {
        Player.Papi -> PapiPuntosTheme.colors.papi
        Player.Mami -> PapiPuntosTheme.colors.mami
    }
    ActionListItem(
        leadingEmoji = action.emoji,
        leadingContainerColor = when (action.beneficiary) {
            Player.Papi -> PapiPuntosTheme.colors.papiSoft
            Player.Mami -> PapiPuntosTheme.colors.mamiSoft
        },
        title = action.label,
        subtitle = stringResource(Res.string.approvals_request_subtitle, action.beneficiary.name),
    ) {
        PointsBadge(
            text = stringResource(Res.string.points_badge, action.points),
            containerColor = accent,
            contentColor = androidx.compose.ui.graphics.Color.White,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = onReject,
            enabled = !isResolving,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(Res.string.approvals_reject))
        }
        OutlinedButton(
            onClick = onApprove,
            enabled = !isResolving,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                stringResource(
                    if (isResolving) Res.string.approvals_saving else Res.string.approvals_approve,
                ),
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun EmptyApprovals() {
    Text(
        text = stringResource(Res.string.approvals_empty),
        style = PapiPuntosTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Preview
@Composable
private fun EmptyApprovalsPreview() {
    PapiPuntosTheme {
        ApprovalsScreenContent(
            actions = emptyList(),
            resolvingActionId = null,
            errorMessage = null,
        )
    }
}
