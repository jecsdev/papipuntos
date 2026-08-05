package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.data.db.AccountEntity
import com.jecsdev.papipuntos.data.db.AuthDao
import com.jecsdev.papipuntos.data.db.ProfileEntity
import com.jecsdev.papipuntos.data.security.PasswordHasher
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.OAuthProvider
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Room-backed auth store (stage 4). Passwords and PINs are persisted only as PBKDF2
 * hashes, never in plain text. On cold start the flow begins in [AuthState.Loading]
 * and [bootstrap] restores it: an account with a live session skips the password
 * screen and lands on the profile picker; logout clears the flag so the password is
 * required again. The per-profile PIN is always re-entered — it is never remembered.
 *
 * Social sign-in rides on Supabase: [signInWithGoogle]/[signInWithApple] only open the
 * browser, and the returning session arrives asynchronously on [SupabaseClient.auth]'s
 * `sessionStatus`, which [scope] observes to link the local account.
 */
class RoomAuthRepository(
    private val dao: AuthDao,
    private val hasher: PasswordHasher,
    private val supabase: SupabaseClient,
    scope: CoroutineScope,
) : AuthRepository {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // The OAuth deep link imports the session into Supabase Auth; we react to that here,
        // rather than in the sign-in call, because the browser round-trip is asynchronous.
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) linkRemoteSession(status.session)
            }
        }
    }

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

    override suspend fun signUp(
        email: String,
        password: String,
    ): Result<Unit> {
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

    override suspend fun logIn(
        email: String,
        password: String,
    ): Result<Unit> {
        val account = dao.getAccount()
        val hash = account?.passwordHash
        val salt = account?.passwordSalt
        // Compare against the trimmed + lowercased form so casing or stray spaces still log in.
        // A remote (Google/Apple) account has no local password — hash/salt are null — so
        // password login simply fails for it, with the same generic message.
        if (account == null ||
            hash == null ||
            salt == null ||
            account.email != email.trim().lowercase() ||
            !hasher.verify(password, salt, hash)
        ) {
            return Result.failure(IllegalStateException("Correo o contraseña incorrectos"))
        }
        dao.setSessionActive(true)
        val profiles = dao.getProfiles().toDomain()
        _state.value = if (profiles.isEmpty()) AuthState.NeedsSetup else AuthState.ProfileSelection(profiles)
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(): Result<Unit> = startOAuth(Google, "Google")

    override suspend fun signInWithApple(): Result<Unit> = startOAuth(Apple, "Apple")

    /**
     * Only launches the provider's consent page. [AuthState] is deliberately untouched here:
     * the session lands later via the deep link and is handled in [linkRemoteSession].
     */
    private suspend fun startOAuth(
        provider: OAuthProvider,
        label: String,
    ): Result<Unit> = try {
        supabase.auth.signInWith(provider)
        Result.success(Unit)
    } catch (cancellation: CancellationException) {
        // Never swallow cancellation — it must keep propagating up the coroutine.
        throw cancellation
    } catch (error: Exception) {
        Result.failure(IllegalStateException("No se pudo iniciar sesión con $label"))
    }

    /**
     * A Supabase session became active (fresh Google/Apple sign-in, or a persisted remote
     * session reloaded on cold start). Mirror that identity into the local account — keyed by
     * the Supabase user id, no local password — and route to setup or the profile picker.
     * We require an email because the whole account model is email-keyed (the provider is
     * configured to always return one).
     */
    private suspend fun linkRemoteSession(session: UserSession) {
        val user = session.user ?: return
        val email = user.email ?: return
        val existing = dao.getAccount()
        dao.upsertAccount(
            AccountEntity(
                email = email.trim().lowercase(),
                // Keep any local password this device already had; a pure remote account has none.
                passwordHash = existing?.passwordHash,
                passwordSalt = existing?.passwordSalt,
                sessionActive = true,
                remoteUserId = user.id,
            ),
        )
        val profiles = dao.getProfiles().toDomain()
        _state.value = if (profiles.isEmpty()) AuthState.NeedsSetup else AuthState.ProfileSelection(profiles)
    }

    override suspend fun saveProfiles(
        papi: NewProfile,
        mami: NewProfile,
    ): Result<Unit> {
        dao.upsertProfiles(listOf(papi.toEntity(), mami.toEntity()))
        _state.value = AuthState.ProfileSelection(dao.getProfiles().toDomain())
        return Result.success(Unit)
    }

    override suspend fun unlockProfile(
        player: Player,
        pin: String,
    ): Result<Unit> {
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
        // Also drop any Supabase session so the observer doesn't restore a remote account on
        // the next launch. Harmless for local-only accounts (nothing to sign out).
        try {
            supabase.auth.signOut()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            // Offline or no active session: the local logout above already stands.
        }
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

    private fun List<ProfileEntity>.toDomain(): List<Profile> = map { it.toDomain() }.sortedBy { it.player.ordinal }

    // Profiles are read back as identity only; the PIN never leaves the store. Unlocking
    // goes through unlockProfile(), which verifies the hash instead of exposing the PIN.
    private fun ProfileEntity.toDomain(): Profile = Profile(player = Player.valueOf(player), name = name, emoji = emoji)
}
