package com.jecsdev.papipuntos.feature.rewards.di

import com.jecsdev.papipuntos.feature.rewards.RedeemViewModel
import com.jecsdev.papipuntos.feature.rewards.RewardsViewModel
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Reward
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rewardsModule = module {
    viewModel { (activePlayer: Player) -> RewardsViewModel(activePlayer, get(), get()) }
    viewModel { (activePlayer: Player, reward: Reward) -> RedeemViewModel(activePlayer, reward, get()) }
}
