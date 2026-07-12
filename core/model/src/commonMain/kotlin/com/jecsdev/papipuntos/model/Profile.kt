package com.jecsdev.papipuntos.model

/**
 * One member of the couple within the shared account. Stage 1 keeps [pin] in
 * plain text in memory only; hashing and persistence land in stage 2.
 */
data class Profile(
    val player: Player,
    val name: String,
    val emoji: String,
    val pin: String,
)
