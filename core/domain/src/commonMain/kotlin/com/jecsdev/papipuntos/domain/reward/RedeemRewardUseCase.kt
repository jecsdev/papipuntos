package com.jecsdev.papipuntos.domain.reward

import com.jecsdev.papipuntos.domain.action.TimeProvider
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Redemption

/** Input for an immediate, local redemption. */
data class RedeemRewardCommand(
    val rewardId: String,
    val player: Player,
    val reason: String,
)

/** Validates user input before the repository performs the atomic balance operation. */
class RedeemRewardUseCase(
    private val repository: RewardRepository,
    private val idGenerator: RedemptionIdGenerator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(command: RedeemRewardCommand): Result<Redemption> {
        if (command.reason.isBlank()) {
            return Result.failure(IllegalArgumentException("Selecciona un motivo para el canje"))
        }
        return repository.redeem(
            RedemptionRequest(
                id = idGenerator.next(),
                rewardId = command.rewardId,
                player = command.player,
                reason = command.reason.trim(),
                createdAtEpochMillis = timeProvider.nowEpochMillis(),
            ),
        )
    }
}

/** Generates local identifiers so redemption records never depend on network access. */
fun interface RedemptionIdGenerator {
    fun next(): String
}
