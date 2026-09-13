package com.jecsdev.papipuntos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** Room access for the seedable reward catalog and the per-profile debit ledger. */
@Dao
interface RewardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCatalog(rewards: List<RewardEntity>)

    @Query("SELECT * FROM reward_catalog ORDER BY cost ASC, id ASC")
    fun observeCatalog(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM reward_catalog WHERE id = :rewardId LIMIT 1")
    suspend fun findReward(rewardId: String): RewardEntity?

    @Query("SELECT * FROM reward_redemption ORDER BY createdAtEpochMillis DESC")
    fun observeRedemptions(): Flow<List<RedemptionEntity>>

    @Query("SELECT COALESCE(SUM(points), 0) FROM action_claim WHERE beneficiary = :player AND status = 'APPROVED'")
    suspend fun approvedPointsFor(player: String): Int

    @Query("SELECT COALESCE(SUM(rewardCost), 0) FROM reward_redemption WHERE player = :player")
    suspend fun redeemedPointsFor(player: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRedemption(redemption: RedemptionEntity)

    /** Uses one DB transaction so two taps cannot spend the same points twice. */
    @Transaction
    suspend fun redeemIfAffordable(
        id: String,
        rewardId: String,
        player: String,
        reason: String,
        createdAtEpochMillis: Long,
    ): RedemptionAttempt {
        val reward = findReward(rewardId) ?: return RedemptionAttempt.MissingReward
        val balance = approvedPointsFor(player) - redeemedPointsFor(player)
        if (balance < reward.cost) return RedemptionAttempt.InsufficientPoints
        val redemption = RedemptionEntity(
            id = id,
            player = player,
            rewardId = reward.id,
            rewardLabel = reward.label,
            rewardCost = reward.cost,
            rewardEmoji = reward.emoji,
            reason = reason,
            createdAtEpochMillis = createdAtEpochMillis,
        )
        insertRedemption(redemption)
        return RedemptionAttempt.Success(redemption)
    }
}

sealed interface RedemptionAttempt {
    data class Success(val redemption: RedemptionEntity) : RedemptionAttempt
    data object MissingReward : RedemptionAttempt
    data object InsufficientPoints : RedemptionAttempt
}
