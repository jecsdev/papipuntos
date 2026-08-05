package com.jecsdev.papipuntos.data.remote

import io.github.jan.supabase.auth.handleDeeplinks
import platform.Foundation.NSURL

/**
 * Hands an incoming OAuth redirect (`usify://auth-callback`) to Supabase Auth so it can exchange
 * the PKCE code and import the session — the iOS counterpart of the Android intent handler.
 *
 * iOS does no automatic redirect handling (its `setupPlatform` is a no-op), so the Swift entry
 * point must call this from `.onOpenURL { }`. For that call to be visible to Swift, the shared
 * framework has to `export(projects.core.data)`.
 */
fun handleAuthDeepLink(url: NSURL) {
    papiPuntosSupabaseClient.handleDeeplinks(url)
}
