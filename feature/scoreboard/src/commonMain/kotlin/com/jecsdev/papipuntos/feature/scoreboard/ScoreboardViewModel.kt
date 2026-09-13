package com.jecsdev.papipuntos.feature.scoreboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Holds persisted action history and derives the current, approved balances. */
class ScoreboardViewModel(
    actionRepository: ActionRepository,
    rewardRepository: RewardRepository,
    private val activePlayer: Player,
    profiles: List<Profile>,
) : ViewModel() {
    private val papi = profiles.find(Player.Papi)
    private val mami = profiles.find(Player.Mami)

    val uiState = combine(
        actionRepository.observeAll(),
        rewardRepository.observeRedemptions(),
    ) { actions, redemptions -> actions.toScoreboardState(redemptions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyScoreboardState(),
        )

    private fun emptyScoreboardState(): ScoreboardUiState = ScoreboardUiState(
        coupleNames = listOfNotNull(papi?.name, mami?.name).joinToString(" & "),
        papiName = papi?.name ?: "Papi",
        mamiName = mami?.name ?: "Mami",
        papiAvatar = papi?.emoji ?: "💙",
        mamiAvatar = mami?.emoji ?: "💗",
        papiPoints = 0,
        mamiPoints = 0,
        goalPoints = DEFAULT_GOAL_POINTS,
        recentActions = emptyList(),
        pendingApprovalCount = 0,
    )

    private fun List<Action>.toScoreboardState(
        redemptions: List<com.jecsdev.papipuntos.model.Redemption>,
    ): ScoreboardUiState {
        val approved = filter { it.status == ActionStatus.APPROVED }
        return emptyScoreboardState().copy(
            papiPoints = approved.pointsFor(Player.Papi) - redemptions.pointsSpentBy(Player.Papi),
            mamiPoints = approved.pointsFor(Player.Mami) - redemptions.pointsSpentBy(Player.Mami),
            recentActions = map { action -> action.toEntry() },
            pendingApprovalCount = count { action ->
                action.status == ActionStatus.PENDING && action.approver == activePlayer
            },
        )
    }

    private fun List<com.jecsdev.papipuntos.model.Redemption>.pointsSpentBy(player: Player): Int =
        filter { it.player == player }.sumOf { it.rewardCost }

    private fun List<Action>.pointsFor(player: Player): Int = filter { it.beneficiary == player }
        .sumOf(Action::points)

    private fun Action.toEntry(): ActionEntry = ActionEntry(
        id = id,
        label = label,
        points = points,
        player = beneficiary,
        timestamp = when (status) {
            ActionStatus.PENDING -> "Pendiente"
            ActionStatus.APPROVED -> "Aprobada"
            ActionStatus.REJECTED -> "Rechazada"
        },
        emoji = emoji,
        rejectionReason = rejectionReason,
    )

    private fun List<Profile>.find(player: Player): Profile? = firstOrNull { it.player == player }

    private companion object {
        const val DEFAULT_GOAL_POINTS = 1_000
    }
}
