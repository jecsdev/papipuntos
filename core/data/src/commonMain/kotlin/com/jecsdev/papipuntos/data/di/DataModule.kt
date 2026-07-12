package com.jecsdev.papipuntos.data.di

import com.jecsdev.papipuntos.data.auth.AuthRepository
import com.jecsdev.papipuntos.data.auth.InMemoryAuthRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { InMemoryAuthRepository() }
}
