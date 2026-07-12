package com.jecsdev.papipuntos.model

/** Drives which top-level screen the app shows, based on local auth progress. */
sealed interface AuthState {
    /** No account signed in yet: show the login/sign-up screen. */
    data object LoggedOut : AuthState

    /** Account exists (or was just created) but has no profiles yet: show profile setup. */
    data object NeedsSetup : AuthState

    /** Account has profiles: show the Netflix-style profile picker / PIN gate. */
    data class ProfileSelection(val profiles: List<Profile>) : AuthState

    /** A profile unlocked its PIN: show the main app for [current]. */
    data class Active(val current: Profile, val profiles: List<Profile>) : AuthState
}
