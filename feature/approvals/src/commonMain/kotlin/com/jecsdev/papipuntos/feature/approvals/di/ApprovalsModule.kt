package com.jecsdev.papipuntos.feature.approvals.di

import com.jecsdev.papipuntos.feature.approvals.ApprovalsViewModel
import com.jecsdev.papipuntos.model.Player
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Registers the pending-approval presentation layer. */
val approvalsModule = module {
    viewModel { (activePlayer: Player) -> ApprovalsViewModel(activePlayer, get(), get()) }
}
