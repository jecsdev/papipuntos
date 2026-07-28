package com.jecsdev.papipuntos.data.di

import com.jecsdev.papipuntos.data.auth.AuthRepository
import com.jecsdev.papipuntos.data.auth.RoomAuthRepository
import com.jecsdev.papipuntos.data.db.AuthDatabase
import com.jecsdev.papipuntos.data.db.buildAuthDatabase
import com.jecsdev.papipuntos.data.remote.papiPuntosSupabaseClient
import com.jecsdev.papipuntos.data.security.PasswordHasher
import com.jecsdev.papipuntos.data.security.Pbkdf2PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Wires the persistent auth stack. The [androidx.room.RoomDatabase.Builder] is provided
 * per platform by `platformAuthModule`, so both modules must be loaded together.
 */
val dataModule = module {
    single { buildAuthDatabase(get()) }
    single { get<AuthDatabase>().authDao() }
    single<PasswordHasher> { Pbkdf2PasswordHasher() }
    // The same instance the Android entry point uses to handle the OAuth redirect.
    single { papiPuntosSupabaseClient }
    // App-lifetime scope: the repository observes the Supabase session on it for as long as
    // the process lives (SupervisorJob so one failure doesn't tear the whole scope down).
    single(named("appScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single<AuthRepository> { RoomAuthRepository(get(), get(), get(), get(named("appScope"))) }
}
