package com.jecsdev.papipuntos.model

/**
 * A profile being created during first-run setup. Carries the plain [pin] only until the
 * repository hashes it; once persisted, profiles are read back as the secret-free [Profile].
 */
data class NewProfile(
    val player: Player,
    val name: String,
    val emoji: String,
    val pin: String,
)
