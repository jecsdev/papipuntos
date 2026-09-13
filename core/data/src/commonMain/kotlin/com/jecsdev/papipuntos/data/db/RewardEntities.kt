package com.jecsdev.papipuntos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local catalog record. Catalog changes never alter prior [RedemptionEntity] snapshots. */
@Entity(tableName = "reward_catalog")
data class RewardEntity(
    @PrimaryKey val id: String,
    val label: String,
    val cost: Int,
    val emoji: String,
)

/** Immutable debit from one profile's balance. */
@Entity(tableName = "reward_redemption")
data class RedemptionEntity(
    @PrimaryKey val id: String,
    val player: String,
    val rewardId: String,
    val rewardLabel: String,
    val rewardCost: Int,
    val rewardEmoji: String,
    val reason: String,
    val createdAtEpochMillis: Long,
)
