package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.StateFlow

/** Local-first auth: one shared account, Netflix-style PIN profiles. Stage 1 is in-memory only. */
interface AuthRepository {
    val state: StateFlow<AuthState>

    /** Cold-start restore: reads persisted state and moves off [AuthState.Loading]. */
    suspend fun bootstrap()

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Unit>
    suspend fun logIn(
        email: String,
        password: String,
    ): Result<Unit>

    /**
     * Opens the provider's consent page in the browser. These return as soon as the browser is
     * launched — success here only means "the flow started". The session arrives later, through
     * the `usify://auth-callback` deep link.
     */
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun signInWithApple(): Result<Unit>

    suspend fun saveProfiles(
        papi: NewProfile,
        mami: NewProfile,
    ): Result<Unit>
    suspend fun unlockProfile(
        player: Player,
        pin: String,
    ): Result<Unit>
    suspend fun logOut()
}
