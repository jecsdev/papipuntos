package com.jecsdev.papipuntos.feature.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ActionIdGenerator
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RewardsUiState(
    val rewards: List<Reward> = emptyList(),
    val balance: Int = 0,
    val errorMessage: String? = null,
)

/** Provides the persisted catalog and active profile's spendable balance. */
class RewardsViewModel(
    private val activePlayer: Player,
    actionRepository: ActionRepository,
    private val rewardRepository: RewardRepository,
    private val idGenerator: ActionIdGenerator,
) : ViewModel() {
    private val catalogReady = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    val uiState = combine(
        actionRepository.observeAll(),
        rewardRepository.observeRedemptions(),
        rewardRepository.observeCatalog(),
        catalogReady,
        errorMessage,
    ) { actions, redemptions, rewards, ready, error ->
        val approved = actions.filter { it.status == ActionStatus.APPROVED && it.beneficiary == activePlayer }
            .sumOf { it.points }
        val spent = redemptions.filter { it.player == activePlayer }.sumOf { it.rewardCost }
        RewardsUiState(rewards = if (ready) rewards else emptyList(), balance = approved - spent, errorMessage = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RewardsUiState())

    init {
        viewModelScope.launch {
            rewardRepository.seedCatalog().onFailure {
                errorMessage.value = it.message ?: "No se pudo abrir el catálogo"
            }
            catalogReady.value = true
        }
    }

    fun createReward(
        label: String,
        cost: String,
        emoji: String,
        onSaved: () -> Unit,
    ) {
        if (_isSaving.value) return
        val points = cost.toIntOrNull()
        when {
            label.isBlank() -> errorMessage.value = "Escribe el nombre de la recompensa"
            points == null || points <= 0 -> errorMessage.value = "Escribe un costo mayor que cero"
            else -> viewModelScope.launch {
                _isSaving.value = true
                try {
                    rewardRepository.addReward(Reward(idGenerator.next(), label.trim(), points, emoji.ifBlank { "🎁" }))
                        .onSuccess {
                            errorMessage.value = null
                            onSaved()
                        }
                        .onFailure { errorMessage.value = it.message ?: "No se pudo guardar la recompensa" }
                } finally {
                    _isSaving.value = false
                }
            }
        }
    }
}
