package com.jecsdev.papipuntos.model

/** A locally managed reward that can be exchanged for a profile's own points. */
data class Reward(
    val id: String,
    val label: String,
    val cost: Int,
    val emoji: String,
)

/** Immutable record of a completed redemption, including the reward as it was redeemed. */
data class Redemption(
    val id: String,
    val player: Player,
    val rewardId: String,
    val rewardLabel: String,
    val rewardCost: Int,
    val rewardEmoji: String,
    val reason: String,
    val createdAtEpochMillis: Long,
)
