package com.jecsdev.papipuntos.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

/**
 * Builds the shared [SupabaseClient] with the Auth (GoTrue) plugin installed. Auth is all
 * we need for now: social/remote sign-in. Postgrest/Realtime get added when the points
 * ledger starts syncing. The Ktor engine is resolved from whichever one is on the platform
 * classpath (CIO on Android, Darwin on iOS).
 */
fun createPapiPuntosSupabaseClient(): SupabaseClient =
    createSupabaseClient(
        supabaseUrl = SupabaseConfig.URL,
        supabaseKey = SupabaseConfig.ANON_KEY,
    ) {
        install(Auth)
    }
