package com.jecsdev.papipuntos.feature.scoreboard.di

import com.jecsdev.papipuntos.feature.scoreboard.ScoreboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scoreboardModule = module {
    viewModel { ScoreboardViewModel() }
}
