package com.jecsdev.papipuntos.feature.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.reward.RedeemRewardCommand
import com.jecsdev.papipuntos.domain.reward.RedeemRewardUseCase
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RedeemUiState(
    val reason: String = "",
    val isRedeeming: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null,
)

/** Runs a one-tap redemption for the active profile only. */
class RedeemViewModel(
    private val activePlayer: Player,
    private val reward: Reward,
    private val redeemReward: RedeemRewardUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RedeemUiState())
    val uiState = _uiState.asStateFlow()

    fun updateReason(reason: String) = _uiState.update { it.copy(reason = reason, errorMessage = null) }

    fun redeem() {
        if (_uiState.value.isRedeeming || _uiState.value.isComplete) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRedeeming = true, errorMessage = null) }
            redeemReward(RedeemRewardCommand(reward.id, activePlayer, _uiState.value.reason))
                .onSuccess { _uiState.update { state -> state.copy(isRedeeming = false, isComplete = true) } }
                .onFailure { error -> _uiState.update { state -> state.copy(isRedeeming = false, errorMessage = error.message) } }
        }
    }
}
