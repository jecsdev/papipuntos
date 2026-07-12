package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.StateFlow

/** Local-first auth: one shared account, Netflix-style PIN profiles. Stage 1 is in-memory only. */
interface AuthRepository {
    val state: StateFlow<AuthState>

    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun logIn(email: String, password: String): Result<Unit>
    suspend fun saveProfiles(papi: Profile, mami: Profile): Result<Unit>
    suspend fun unlockProfile(player: Player, pin: String): Result<Unit>
    fun logOut()
}
