package com.jecsdev.papipuntos.model

/**
 * A single email account shared by the couple (Netflix-style: one login, many
 * profiles). Stage 1 keeps [password] in plain text in memory only; hashing and
 * persistence land in stage 2.
 */
data class Account(
    val email: String,
    val password: String,
)
