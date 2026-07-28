package com.jecsdev.papipuntos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The single shared account. Only one row ever exists, so the primary key is a
 * fixed [id] of 0. A local (email/password) account stores its secret as a PBKDF2
 * hash + salt; a remote account signed in through Google/Apple has NO local password,
 * so those columns are null and [remoteUserId] holds the Supabase user id instead.
 * [sessionActive] persists whether the account is currently logged in, so a cold
 * start can skip the password screen (true) or require it again after logout (false).
 */
@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val email: String,
    val passwordHash: String? = null,
    val passwordSalt: String? = null,
    val sessionActive: Boolean = false,
    val remoteUserId: String? = null,
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
