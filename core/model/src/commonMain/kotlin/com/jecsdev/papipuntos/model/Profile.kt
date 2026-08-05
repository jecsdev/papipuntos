package com.jecsdev.papipuntos.model

/**
 * One member of the couple within the shared account, as identity only. The PIN is a
 * secret and never travels on this type; creating a profile uses [NewProfile], and
 * unlocking verifies the PIN through the repository.
 */
data class Profile(
    val player: Player,
    val name: String,
    val emoji: String,
)
