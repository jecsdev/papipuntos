package com.jecsdev.papipuntos.feature.rewards.model

/** A reward a profile can redeem its points for. */
data class Reward(
    val id: String,
    val label: String,
    val cost: Int,
    val emoji: String,
)
