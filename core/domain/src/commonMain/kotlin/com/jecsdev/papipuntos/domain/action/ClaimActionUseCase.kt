package com.jecsdev.papipuntos.domain.action

import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.other

/** Input accepted by [ClaimActionUseCase], independent from a feature's UI model. */
data class ClaimActionCommand(
    val label: String,
    val points: Int,
    val emoji: String,
    val beneficiary: Player,
)

/** Creates stable identifiers without coupling the use case to a platform implementation. */
fun interface ActionIdGenerator {
    fun next(): String
}

/** Supplies time to the use case and makes timestamp-dependent tests deterministic. */
fun interface TimeProvider {
    fun nowEpochMillis(): Long
}

/**
 * Creates a pending request. The beneficiary receives points only after the other profile
 * approves it; no balance mutation is allowed at this stage.
 */
class ClaimActionUseCase(
    private val repository: ActionRepository,
    private val idGenerator: ActionIdGenerator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(command: ClaimActionCommand): Result<Action> {
        val label = command.label.trim()
        return when {
            label.isBlank() -> Result.failure(IllegalArgumentException("La acción necesita un nombre"))
            command.points <= 0 -> Result.failure(IllegalArgumentException("Los puntos deben ser mayores que cero"))
            command.emoji.isBlank() -> Result.failure(IllegalArgumentException("La acción necesita un emoji"))
            else -> {
                val action = Action(
                    id = idGenerator.next(),
                    label = label,
                    points = command.points,
                    emoji = command.emoji,
                    beneficiary = command.beneficiary,
                    approver = command.beneficiary.other(),
                    status = ActionStatus.PENDING,
                    createdAtEpochMillis = timeProvider.nowEpochMillis(),
                )
                repository.create(action).map { action }
            }
        }
    }
}
