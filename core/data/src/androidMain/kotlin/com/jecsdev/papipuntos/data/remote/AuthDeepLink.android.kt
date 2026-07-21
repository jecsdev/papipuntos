package com.jecsdev.papipuntos.data.remote

import android.content.Intent
import io.github.jan.supabase.auth.handleDeeplinks

/**
 * Hands an incoming OAuth redirect (`usify://auth-callback`) to Supabase Auth so it can exchange
 * the PKCE code and import the session. Safe to call with any intent: Supabase ignores the ones
 * whose scheme/host don't match the configured deep link.
 *
 * Supabase's Android platform setup only wires session refresh on lifecycle events, never the
 * redirect itself, so the activity must call this explicitly. Kept here so `:androidApp` doesn't
 * need to depend on supabase-kt directly.
 */
fun handleAuthDeepLink(intent: Intent) {
    papiPuntosSupabaseClient.handleDeeplinks(intent)
}
