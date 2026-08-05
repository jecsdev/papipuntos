package com.jecsdev.papipuntos.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient

/**
 * Scheme and host of the OAuth redirect deep link (`usify://auth-callback`).
 *
 * The same value is unavoidably duplicated in three places that cannot read Kotlin constants:
 * the Supabase dashboard (Auth -> URL Configuration -> Redirect URLs), the Android
 * intent-filter, and the iOS CFBundleURLSchemes. Change one, change all four — otherwise the
 * browser has nowhere valid to hand control back to and social sign-in silently breaks.
 */
const val AUTH_CALLBACK_SCHEME = "usify"
const val AUTH_CALLBACK_HOST = "auth-callback"

/**
 * The one shared [SupabaseClient]. It is a lazy top-level singleton rather than a Koin-only
 * binding because the Android entry point needs this exact instance to hand the OAuth redirect
 * back to Auth, and Koin here is scoped to the Compose tree (see `App.kt`).
 *
 * Auth is the only plugin for now; Postgrest/Realtime arrive when the points ledger syncs.
 * The Ktor engine is picked from the platform classpath (CIO on Android, Darwin on iOS).
 */
val papiPuntosSupabaseClient: SupabaseClient by lazy { createPapiPuntosSupabaseClient() }

private fun createPapiPuntosSupabaseClient(): SupabaseClient = createSupabaseClient(
    supabaseUrl = SupabaseConfig.URL,
    supabaseKey = SupabaseConfig.ANON_KEY,
) {
    install(Auth) {
        scheme = AUTH_CALLBACK_SCHEME
        host = AUTH_CALLBACK_HOST
        // PKCE instead of the default implicit flow: the redirect carries a short-lived code
        // that is exchanged for the session, rather than tokens riding in the URL fragment.
        flowType = FlowType.PKCE
    }
}
