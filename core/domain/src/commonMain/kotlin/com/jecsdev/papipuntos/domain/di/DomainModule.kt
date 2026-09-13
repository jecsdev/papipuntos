package com.jecsdev.papipuntos.domain.di

import com.jecsdev.papipuntos.domain.action.ClaimActionUseCase
import com.jecsdev.papipuntos.domain.action.ResolveActionUseCase
import com.jecsdev.papipuntos.domain.reward.RedeemRewardUseCase
import org.koin.dsl.module

/** Registers domain orchestration only; persistence implementations remain in the data layer. */
val domainModule = module {
    factory { ClaimActionUseCase(get(), get(), get()) }
    factory { ResolveActionUseCase(get(), get()) }
    factory { RedeemRewardUseCase(get(), get(), get()) }
}
