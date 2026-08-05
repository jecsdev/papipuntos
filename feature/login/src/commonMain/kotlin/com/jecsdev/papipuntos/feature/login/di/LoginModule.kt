package com.jecsdev.papipuntos.feature.login.di

import com.jecsdev.papipuntos.feature.login.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel { AuthViewModel(get()) }
}
