package com.jecsdev.papipuntos.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.data.auth.AuthRepository
import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
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
        AuthState.LoggedOut,
    )

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

    fun saveProfiles(papi: Profile, mami: Profile) = viewModelScope.launch {
        repository.saveProfiles(papi, mami)
            .onSuccess { _error.value = null }
    }

    fun unlockProfile(player: Player, pin: String) = viewModelScope.launch {
        repository.unlockProfile(player, pin)
            .onSuccess { _error.value = null }
            .onFailure { _error.value = it.message }
    }

    fun logOut() = repository.logOut()

    fun clearError() {
        _error.value = null
    }
}
