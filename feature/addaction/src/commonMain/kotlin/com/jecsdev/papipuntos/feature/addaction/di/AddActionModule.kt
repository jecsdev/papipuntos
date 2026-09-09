package com.jecsdev.papipuntos.feature.addaction.di

import com.jecsdev.papipuntos.feature.addaction.AddActionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Registers the add-action presentation layer. */
val addActionModule = module {
    viewModel { AddActionViewModel(get()) }
}
