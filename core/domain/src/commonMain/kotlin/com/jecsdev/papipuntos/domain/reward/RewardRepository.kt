package com.jecsdev.papipuntos.domain.reward

import com.jecsdev.papipuntos.model.Redemption
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.Flow

/** Persistence boundary for the local reward catalog and immutable redemption ledger. */
interface RewardRepository {
    suspend fun seedCatalog(): Result<Unit>
    fun observeCatalog(): Flow<List<Reward>>
    fun observeRedemptions(): Flow<List<Redemption>>

    /** Atomically verifies the profile balance and inserts a redemption snapshot. */
    suspend fun redeem(request: RedemptionRequest): Result<Redemption>
}

/** Fully formed persistence request, created only by [RedeemRewardUseCase]. */
data class RedemptionRequest(
    val id: String,
    val rewardId: String,
    val player: com.jecsdev.papipuntos.model.Player,
    val reason: String,
    val createdAtEpochMillis: Long,
)
