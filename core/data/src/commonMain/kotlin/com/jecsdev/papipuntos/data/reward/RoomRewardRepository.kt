package com.jecsdev.papipuntos.data.reward

import com.jecsdev.papipuntos.data.db.RedemptionAttempt
import com.jecsdev.papipuntos.data.db.RedemptionEntity
import com.jecsdev.papipuntos.data.db.RewardDao
import com.jecsdev.papipuntos.data.db.RewardEntity
import com.jecsdev.papipuntos.domain.reward.RedemptionRequest
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Redemption
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Local-first reward repository; catalog seeding is repeatable and preserves all debits. */
class RoomRewardRepository(private val dao: RewardDao) : RewardRepository {
    override suspend fun seedCatalog(): Result<Unit> = runCatching { dao.upsertCatalog(LocalRewardCatalog.rewards) }

    override fun observeCatalog(): Flow<List<Reward>> = dao.observeCatalog().map { rewards ->
        rewards.map(RewardEntity::toDomain)
    }

    override fun observeRedemptions(): Flow<List<Redemption>> = dao.observeRedemptions().map { redemptions ->
        redemptions.map(RedemptionEntity::toDomain)
    }

    override suspend fun redeem(request: RedemptionRequest): Result<Redemption> = runCatching {
        when (val attempt = dao.redeemIfAffordable(
            id = request.id,
            rewardId = request.rewardId,
            player = request.player.name,
            reason = request.reason,
            createdAtEpochMillis = request.createdAtEpochMillis,
        )) {
            is RedemptionAttempt.Success -> attempt.redemption.toDomain()
            RedemptionAttempt.MissingReward -> throw IllegalArgumentException("Recompensa no encontrada")
            RedemptionAttempt.InsufficientPoints -> throw IllegalStateException("No tienes puntos suficientes")
        }
    }
}

/** Existing mock catalog moved to persistent local data without changing its visible choices. */
private object LocalRewardCatalog {
    val rewards = listOf(
        RewardEntity("r1", "Noche de película elegida por mí", 500, "🎬"),
        RewardEntity("r2", "Masaje de 30 minutos", 600, "💆"),
        RewardEntity("r3", "Desayuno en la cama", 400, "☕"),
        RewardEntity("r4", "Un día sin quejarme", 300, "😎"),
        RewardEntity("r5", "Escoger dónde comer", 350, "🍔"),
        RewardEntity("r6", "Dormir una hora más", 250, "🛏️"),
        RewardEntity("r7", "Noche de gaming sin reclamos", 450, "🎮"),
        RewardEntity("r8", "Salida de compras sin límite de tiempo", 700, "🛍️"),
    )
}

private fun RewardEntity.toDomain(): Reward = Reward(id, label, cost, emoji)

private fun RedemptionEntity.toDomain(): Redemption = Redemption(
    id = id,
    player = Player.valueOf(player),
    rewardId = rewardId,
    rewardLabel = rewardLabel,
    rewardCost = rewardCost,
    rewardEmoji = rewardEmoji,
    reason = reason,
    createdAtEpochMillis = createdAtEpochMillis,
)
