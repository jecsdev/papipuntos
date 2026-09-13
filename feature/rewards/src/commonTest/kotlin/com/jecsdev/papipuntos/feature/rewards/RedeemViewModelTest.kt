package com.jecsdev.papipuntos.feature.rewards

import com.jecsdev.papipuntos.domain.action.TimeProvider
import com.jecsdev.papipuntos.domain.reward.RedemptionIdGenerator
import com.jecsdev.papipuntos.domain.reward.RedemptionRequest
import com.jecsdev.papipuntos.domain.reward.RedeemRewardUseCase
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Redemption
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RedeemViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun sends_the_selected_reason_for_the_active_profile_and_exposes_completion() = runTest {
        val repository = RecordingRewardRepository()
        val reward = Reward("r1", "Película", 500, "🎬")
        val viewModel = RedeemViewModel(
            activePlayer = Player.Mami,
            reward = reward,
            redeemReward = RedeemRewardUseCase(repository, RedemptionIdGenerator { "redemption-1" }, TimeProvider { 50L }),
        )

        viewModel.updateReason("Celebración especial")
        viewModel.redeem()

        assertEquals(Player.Mami, repository.request?.player)
        assertEquals("Celebración especial", repository.request?.reason)
        assertTrue(viewModel.uiState.value.isComplete)
    }

    private class RecordingRewardRepository : RewardRepository {
        var request: RedemptionRequest? = null
        override suspend fun seedCatalog(): Result<Unit> = Result.success(Unit)
        override fun observeCatalog(): Flow<List<Reward>> = emptyFlow()
        override fun observeRedemptions(): Flow<List<Redemption>> = emptyFlow()
        override suspend fun redeem(request: RedemptionRequest): Result<Redemption> {
            this.request = request
            return Result.success(Redemption(request.id, request.player, request.rewardId, "Película", 500, "🎬", request.reason, request.createdAtEpochMillis))
        }
    }
}
