package com.jecsdev.papipuntos.feature.profile.di

import com.jecsdev.papipuntos.feature.profile.ProfileViewModel
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { (activePlayer: Player, profiles: List<Profile>) ->
        ProfileViewModel(activePlayer, profiles, get(), get())
    }
}
