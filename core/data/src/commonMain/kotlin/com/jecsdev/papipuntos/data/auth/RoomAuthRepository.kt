package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.data.db.AccountEntity
import com.jecsdev.papipuntos.data.db.AuthDao
import com.jecsdev.papipuntos.data.db.ProfileEntity
import com.jecsdev.papipuntos.data.security.PasswordHasher
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Room-backed auth store (stage 3). Passwords and PINs are persisted only as PBKDF2
 * hashes, never in plain text. On cold start the flow begins in [AuthState.Loading]
 * and [bootstrap] restores it: an account with a live session skips the password
 * screen and lands on the profile picker; logout clears the flag so the password is
 * required again. The per-profile PIN is always re-entered — it is never remembered.
 */
class RoomAuthRepository(
    private val dao: AuthDao,
    private val hasher: PasswordHasher,
) : AuthRepository {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    override suspend fun bootstrap() {
        val account = dao.getAccount()
        _state.value = when {
            account == null || !account.sessionActive -> AuthState.LoggedOut
            else -> {
                val profiles = dao.getProfiles().toDomain()
                if (profiles.isEmpty()) AuthState.NeedsSetup else AuthState.ProfileSelection(profiles)
            }
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa correo y contraseña"))
        }
        if (dao.getAccount() != null) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con este correo"))
        }
        val hashed = hasher.hash(password)
        // A fresh sign-up opens a live session so onboarding (profile setup) survives a restart.
        // Email is trimmed + lowercased so casing or stray spaces can't split one identity into two accounts.
        dao.upsertAccount(
            AccountEntity(
                email = email.trim().lowercase(),
                passwordHash = hashed.hashHex,
                passwordSalt = hashed.saltHex,
                sessionActive = true,
            ),
        )
        _state.value = AuthState.NeedsSetup
        return Result.success(Unit)
    }

    override suspend fun logIn(email: String, password: String): Result<Unit> {
        val account = dao.getAccount()
        // Compare against the trimmed + lowercased form so casing or stray spaces still log in.
        if (account == null ||
            account.email != email.trim().lowercase() ||
            !hasher.verify(password, account.passwordSalt, account.passwordHash)
        ) {
            return Result.failure(IllegalStateException("Correo o contraseña incorrectos"))
        }
        dao.setSessionActive(true)
        val profiles = dao.getProfiles().toDomain()
        _state.value = if (profiles.isEmpty()) AuthState.NeedsSetup else AuthState.ProfileSelection(profiles)
        return Result.success(Unit)
    }

    override suspend fun saveProfiles(papi: NewProfile, mami: NewProfile): Result<Unit> {
        dao.upsertProfiles(listOf(papi.toEntity(), mami.toEntity()))
        _state.value = AuthState.ProfileSelection(dao.getProfiles().toDomain())
        return Result.success(Unit)
    }

    override suspend fun unlockProfile(player: Player, pin: String): Result<Unit> {
        val profile = dao.getProfile(player.name)
            ?: return Result.failure(IllegalStateException("Perfil no encontrado"))
        if (!hasher.verify(pin, profile.pinSalt, profile.pinHash)) {
            return Result.failure(IllegalStateException("PIN incorrecto"))
        }
        _state.value = AuthState.Active(current = profile.toDomain(), profiles = dao.getProfiles().toDomain())
        return Result.success(Unit)
    }

    override suspend fun logOut() {
        // Clear the persisted session so the next cold start requires the password again.
        // Account and profiles stay on disk untouched.
        dao.setSessionActive(false)
        _state.value = AuthState.LoggedOut
    }

    private fun NewProfile.toEntity(): ProfileEntity {
        val hashed = hasher.hash(pin)
        return ProfileEntity(
            player = player.name,
            name = name,
            emoji = emoji,
            pinHash = hashed.hashHex,
            pinSalt = hashed.saltHex,
        )
    }

    private fun List<ProfileEntity>.toDomain(): List<Profile> =
        map { it.toDomain() }.sortedBy { it.player.ordinal }

    // Profiles are read back as identity only; the PIN never leaves the store. Unlocking
    // goes through unlockProfile(), which verifies the hash instead of exposing the PIN.
    private fun ProfileEntity.toDomain(): Profile =
        Profile(player = Player.valueOf(player), name = name, emoji = emoji)
}
