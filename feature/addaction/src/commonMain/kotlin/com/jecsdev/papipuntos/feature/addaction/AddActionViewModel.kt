package com.jecsdev.papipuntos.feature.addaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ClaimActionCommand
import com.jecsdev.papipuntos.domain.action.ClaimActionUseCase
import com.jecsdev.papipuntos.feature.addaction.model.SuggestedAction
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI state for the add-action form. Suggestions stay in the UI layer because they are catalog data. */
data class AddActionUiState(
    val target: Player = Player.Papi,
    val query: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

/** One-off UI effects that must not be replayed after a configuration change. */
sealed interface AddActionEvent {
    data class Submitted(
        val beneficiary: Player,
        val suggestion: SuggestedAction,
    ) : AddActionEvent
}

/** Converts user intent into a pending point request through the domain use case. */
class AddActionViewModel(
    private val claimAction: ClaimActionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddActionUiState())
    val uiState = _uiState.asStateFlow()

    private val eventChannel = Channel<AddActionEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun selectTarget(target: Player) {
        _uiState.update { it.copy(target = target, errorMessage = null) }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun submit(suggestion: SuggestedAction) {
        val target = uiState.value.target
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = claimAction(
                ClaimActionCommand(
                    label = suggestion.label,
                    points = suggestion.points,
                    emoji = suggestion.emoji,
                    beneficiary = target,
                ),
            )
            if (result.isSuccess) {
                eventChannel.send(AddActionEvent.Submitted(target, suggestion))
            } else {
                val error = result.exceptionOrNull()
                _uiState.update { it.copy(errorMessage = error?.message ?: "No se pudo guardar la acción") }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }
}
