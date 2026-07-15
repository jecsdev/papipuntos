package com.jecsdev.papipuntos.data.di

import com.jecsdev.papipuntos.data.auth.AuthRepository
import com.jecsdev.papipuntos.data.auth.RoomAuthRepository
import com.jecsdev.papipuntos.data.db.AuthDatabase
import com.jecsdev.papipuntos.data.db.buildAuthDatabase
import com.jecsdev.papipuntos.data.security.PasswordHasher
import com.jecsdev.papipuntos.data.security.Pbkdf2PasswordHasher
import org.koin.dsl.module

/**
 * Wires the persistent auth stack. The [androidx.room.RoomDatabase.Builder] is provided
 * per platform by `platformAuthModule`, so both modules must be loaded together.
 */
val dataModule = module {
    single { buildAuthDatabase(get()) }
    single { get<AuthDatabase>().authDao() }
    single<PasswordHasher> { Pbkdf2PasswordHasher() }
    single<AuthRepository> { RoomAuthRepository(get(), get()) }
}
