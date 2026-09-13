package com.jecsdev.papipuntos.feature.approvals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ActionDecision
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.action.ResolveActionCommand
import com.jecsdev.papipuntos.domain.action.ResolveActionUseCase
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** State for requests awaiting the active profile's decision. */
data class ApprovalsUiState(
    val actions: List<Action> = emptyList(),
    val resolvingActionId: String? = null,
    val errorMessage: String? = null,
)

/** Exposes pending requests and sends approval decisions through the domain layer. */
class ApprovalsViewModel(
    private val activePlayer: Player,
    actionRepository: ActionRepository,
    private val resolveAction: ResolveActionUseCase,
) : ViewModel() {
    private val resolvingActionId = MutableStateFlow<String?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ApprovalsUiState> = combine(
        actionRepository.observePendingFor(activePlayer),
        resolvingActionId,
        errorMessage,
    ) { actions, resolvingId, error ->
        ApprovalsUiState(
            actions = actions,
            resolvingActionId = resolvingId,
            errorMessage = error,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ApprovalsUiState(),
        )

    fun decide(
        action: Action,
        decision: ActionDecision,
    ) {
        if (uiState.value.resolvingActionId != null) return
        viewModelScope.launch {
            resolvingActionId.value = action.id
            errorMessage.value = null
            val result = resolveAction(
                ResolveActionCommand(
                    actionId = action.id,
                    reviewer = activePlayer,
                    decision = decision,
                ),
            )
            if (result.isFailure) {
                errorMessage.value = result.exceptionOrNull()?.message ?: "No se pudo actualizar la acción"
            }
            resolvingActionId.value = null
        }
    }
}
