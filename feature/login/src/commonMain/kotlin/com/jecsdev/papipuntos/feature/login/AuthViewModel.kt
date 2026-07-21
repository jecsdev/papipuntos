package com.jecsdev.papipuntos.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.data.auth.AuthRepository
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Bridges the three auth screens (login, profile setup, profile picker) to [AuthRepository]. */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AuthState.Loading,
    )

    init {
        // Restore the persisted session on cold start; the flow stays on Loading until this resolves.
        viewModelScope.launch { repository.bootstrap() }
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Each action clears the error on success so a stale message from an earlier
    // step (e.g. a failed sign-up) never bleeds into the next screen's UI.
    fun signUp(email: String, password: String) = viewModelScope.launch {
        repository.signUp(email, password)
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    fun logIn(email: String, password: String) = viewModelScope.launch {
        repository.logIn(email, password)
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    // These only open the browser. The session comes back through the deep link, so there is
    // no success state to react to here — only a failure worth showing.
    fun signInWithGoogle() = viewModelScope.launch {
        repository.signInWithGoogle()
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    fun signInWithApple() = viewModelScope.launch {
        repository.signInWithApple()
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    fun saveProfiles(papi: NewProfile, mami: NewProfile) = viewModelScope.launch {
        repository.saveProfiles(papi, mami)
            .onSuccess { _error.value = null }
    }

    fun unlockProfile(player: Player, pin: String) = viewModelScope.launch {
        repository.unlockProfile(player, pin)
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    fun logOut() = viewModelScope.launch { repository.logOut() }

    fun clearError() {
        _error.value = null
    }
}
