package com.jecsdev.papipuntos.domain.action

import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player

/** A reviewer decision that can move a request out of the pending state. */
enum class ActionDecision {
    Approve,
    Reject,
}

/** Input for resolving a pending request. */
data class ResolveActionCommand(
    val actionId: String,
    val reviewer: Player,
    val decision: ActionDecision,
    val rejectionReason: String = "",
)

/** Applies an approval or rejection while enforcing the assigned-reviewer invariant. */
class ResolveActionUseCase(
    private val repository: ActionRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(command: ResolveActionCommand): Result<Action> {
        val action = repository.findById(command.actionId)
            ?: return Result.failure(IllegalArgumentException("No encontramos esta acción"))
        if (action.status != ActionStatus.PENDING) {
            return Result.failure(IllegalStateException("Esta acción ya fue resuelta"))
        }
        if (action.approver != command.reviewer) {
            return Result.failure(IllegalStateException("No puedes revisar esta acción"))
        }

        val resolved = action.copy(
            status = command.decision.toStatus(),
            resolvedAtEpochMillis = timeProvider.nowEpochMillis(),
            rejectionReason = command.rejectionReason.trim().takeIf {
                command.decision == ActionDecision.Reject && it.isNotBlank()
            },
        )
        return repository.resolveIfPending(resolved).fold(
            onSuccess = { updated ->
                if (updated) {
                    Result.success(resolved)
                } else {
                    Result.failure(IllegalStateException("Esta acción ya fue resuelta"))
                }
            },
            onFailure = { error -> Result.failure(error) },
        )
    }

    private fun ActionDecision.toStatus(): ActionStatus = when (this) {
        ActionDecision.Approve -> ActionStatus.APPROVED
        ActionDecision.Reject -> ActionStatus.REJECTED
    }
}
