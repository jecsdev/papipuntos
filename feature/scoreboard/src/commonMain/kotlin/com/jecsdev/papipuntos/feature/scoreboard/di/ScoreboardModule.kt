package com.jecsdev.papipuntos.feature.scoreboard.di

import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardViewModel
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scoreboardModule = module {
    viewModel { (activePlayer: Player, profiles: List<Profile>) ->
        ScoreboardViewModel(get(), activePlayer, profiles)
    }
}
