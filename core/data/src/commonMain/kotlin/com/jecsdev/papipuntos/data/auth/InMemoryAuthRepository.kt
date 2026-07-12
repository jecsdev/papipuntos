package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.model.Account
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stage 1 implementation: everything lives in process memory only. Nothing is
 * persisted across app restarts; that lands with real storage in stage 2.
 */
class InMemoryAuthRepository : AuthRepository {

    private val _state = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    private var account: Account? = null
    private var profiles: List<Profile> = emptyList()

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa correo y contraseña"))
        }
        if (account != null) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con este correo"))
        }
        account = Account(email, password)
        profiles = emptyList()
        _state.value = AuthState.NeedsSetup
        return Result.success(Unit)
    }

    override suspend fun logIn(email: String, password: String): Result<Unit> {
        val currentAccount = account
        if (currentAccount == null || currentAccount.email != email || currentAccount.password != password) {
            return Result.failure(IllegalStateException("Correo o contraseña incorrectos"))
        }
        _state.value = if (profiles.isEmpty()) {
            AuthState.NeedsSetup
        } else {
            AuthState.ProfileSelection(profiles)
        }
        return Result.success(Unit)
    }

    override suspend fun saveProfiles(papi: Profile, mami: Profile): Result<Unit> {
        profiles = listOf(papi, mami)
        _state.value = AuthState.ProfileSelection(profiles)
        return Result.success(Unit)
    }

    override suspend fun unlockProfile(player: Player, pin: String): Result<Unit> {
        val profile = profiles.firstOrNull { it.player == player }
            ?: return Result.failure(IllegalStateException("Perfil no encontrado"))
        if (profile.pin != pin) {
            return Result.failure(IllegalStateException("PIN incorrecto"))
        }
        _state.value = AuthState.Active(profile, profiles)
        return Result.success(Unit)
    }

    override fun logOut() {
        // Keep `account` and `profiles` around so re-login works within the same process session.
        _state.value = AuthState.LoggedOut
    }
}
