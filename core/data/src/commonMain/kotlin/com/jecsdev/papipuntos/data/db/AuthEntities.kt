package com.jecsdev.papipuntos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The single shared account. Only one row ever exists, so the primary key is a
 * fixed [id] of 0. Secrets are stored as PBKDF2 hash + salt, never in plain text.
 */
@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}

/**
 * One profile of the couple, keyed by [Player.name] ("Papi"/"Mami"). The PIN is
 * stored as PBKDF2 hash + salt, never in plain text.
 */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val player: String,
    val name: String,
    val emoji: String,
    val pinHash: String,
    val pinSalt: String,
)
