package com.jecsdev.papipuntos.domain.reward

import com.jecsdev.papipuntos.domain.action.TimeProvider
import com.jecsdev.papipuntos.model.Redemption
import com.jecsdev.papipuntos.model.Reward
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RedeemRewardUseCaseTest {
    @Test
    fun creates_a_timestamped_request_with_a_trimmed_reason() = runBlocking {
        val repository = RecordingRewardRepository()
        val result = RedeemRewardUseCase(repository, RedemptionIdGenerator { "redemption-1" }, TimeProvider { 42L })(
            RedeemRewardCommand("r1", Player.Mami, "  Semana especial  "),
        )

        assertTrue(result.isSuccess)
        assertEquals("redemption-1", repository.request?.id)
        assertEquals("Semana especial", repository.request?.reason)
        assertEquals(42L, repository.request?.createdAtEpochMillis)
    }

    @Test
    fun rejects_a_blank_reason_without_calling_the_repository() = runBlocking {
        val repository = RecordingRewardRepository()
        val result = RedeemRewardUseCase(repository, RedemptionIdGenerator { "unused" }, TimeProvider { 1L })(
            RedeemRewardCommand("r1", Player.Papi, " "),
        )

        assertFalse(result.isSuccess)
        assertEquals("Selecciona un motivo para el canje", result.exceptionOrNull()?.message)
        assertEquals(null, repository.request)
    }

    private class RecordingRewardRepository : RewardRepository {
        var request: RedemptionRequest? = null

        override suspend fun seedCatalog(): Result<Unit> = Result.success(Unit)
        override fun observeCatalog(): Flow<List<Reward>> = emptyFlow()
        override fun observeRedemptions(): Flow<List<Redemption>> = emptyFlow()
        override suspend fun redeem(request: RedemptionRequest): Result<Redemption> {
            this.request = request
            return Result.success(
                Redemption(request.id, request.player, request.rewardId, "Test", 50, "✨", request.reason, request.createdAtEpochMillis),
            )
        }
    }
}
