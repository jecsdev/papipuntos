package com.jecsdev.papipuntos.feature.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RewardsUiState(
    val rewards: List<Reward> = emptyList(),
    val balance: Int = 0,
)

/** Provides the persisted catalog and active profile's spendable balance. */
class RewardsViewModel(
    private val activePlayer: Player,
    actionRepository: ActionRepository,
    rewardRepository: RewardRepository,
) : ViewModel() {
    private val catalogReady = MutableStateFlow(false)

    val uiState = combine(
        actionRepository.observeAll(),
        rewardRepository.observeRedemptions(),
        rewardRepository.observeCatalog(),
        catalogReady,
    ) { actions, redemptions, rewards, ready ->
        val approved = actions.filter { it.status == ActionStatus.APPROVED && it.beneficiary == activePlayer }
            .sumOf { it.points }
        val spent = redemptions.filter { it.player == activePlayer }.sumOf { it.rewardCost }
        RewardsUiState(rewards = if (ready) rewards else emptyList(), balance = approved - spent)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RewardsUiState())

    init {
        viewModelScope.launch {
            rewardRepository.seedCatalog()
            catalogReady.value = true
        }
    }
}
