package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.model.Account
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory test double: everything lives in process memory only, nothing survives a
 * restart. Mirrors [RoomAuthRepository]'s contract (including the session flag and
 * [bootstrap]) so it can stand in for previews and tests.
 */
class InMemoryAuthRepository : AuthRepository {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    private var account: Account? = null
    private var profiles: List<NewProfile> = emptyList()
    private var sessionActive: Boolean = false

    override suspend fun bootstrap() {
        val currentAccount = account
        _state.value = when {
            currentAccount == null || !sessionActive -> AuthState.LoggedOut
            profiles.isEmpty() -> AuthState.NeedsSetup
            else -> AuthState.ProfileSelection(profiles.toIdentity())
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa correo y contraseña"))
        }
        if (account != null) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con este correo"))
        }
        account = Account(email.trim().lowercase(), password)
        profiles = emptyList()
        sessionActive = true
        _state.value = AuthState.NeedsSetup
        return Result.success(Unit)
    }

    override suspend fun logIn(email: String, password: String): Result<Unit> {
        val currentAccount = account
        if (currentAccount == null || currentAccount.email != email.trim().lowercase() || currentAccount.password != password) {
            return Result.failure(IllegalStateException("Correo o contraseña incorrectos"))
        }
        sessionActive = true
        _state.value = if (profiles.isEmpty()) {
            AuthState.NeedsSetup
        } else {
            AuthState.ProfileSelection(profiles.toIdentity())
        }
        return Result.success(Unit)
    }

    override suspend fun saveProfiles(papi: NewProfile, mami: NewProfile): Result<Unit> {
        profiles = listOf(papi, mami)
        _state.value = AuthState.ProfileSelection(profiles.toIdentity())
        return Result.success(Unit)
    }

    override suspend fun unlockProfile(player: Player, pin: String): Result<Unit> {
        val profile = profiles.firstOrNull { it.player == player }
            ?: return Result.failure(IllegalStateException("Perfil no encontrado"))
        if (profile.pin != pin) {
            return Result.failure(IllegalStateException("PIN incorrecto"))
        }
        _state.value = AuthState.Active(profile.toIdentity(), profiles.toIdentity())
        return Result.success(Unit)
    }

    private fun NewProfile.toIdentity(): Profile = Profile(player, name, emoji)

    private fun List<NewProfile>.toIdentity(): List<Profile> = map { it.toIdentity() }

    override suspend fun logOut() {
        // Keep `account` and `profiles` around so re-login works within the same process session;
        // only the session flag drops, matching the persisted store's logout.
        sessionActive = false
        _state.value = AuthState.LoggedOut
    }
}
