package com.jecsdev.papipuntos.data.reward

import com.jecsdev.papipuntos.data.db.RedemptionEntity
import com.jecsdev.papipuntos.data.db.RewardDao
import com.jecsdev.papipuntos.data.db.RewardEntity
import com.jecsdev.papipuntos.domain.reward.RedemptionRequest
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Reward
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoomRewardRepositoryTest {
    @Test
    fun seeds_catalog_idempotently_and_maps_the_immutable_redemption_snapshot() = runBlocking {
        val dao = FakeRewardDao()
        val repository = RoomRewardRepository(dao)

        repository.seedCatalog()
        repository.seedCatalog()
        repository.addReward(Reward("custom-1", "Cena especial", 200, "🍽️")).getOrThrow()
        repository.seedCatalog()
        val result = repository.redeem(
            RedemptionRequest("redemption-1", "r1", Player.Papi, "Celebración", 100L),
        )

        assertEquals(9, dao.catalog.value.size)
        assertEquals("Cena especial", dao.catalog.value.first { it.id == "custom-1" }.label)
        assertTrue(result.isSuccess)
        assertEquals("Noche de película elegida por mí", result.getOrThrow().rewardLabel)
        assertEquals("Celebración", result.getOrThrow().reason)

        assertTrue(
            repository.redeem(
                RedemptionRequest("redemption-2", "r1", Player.Papi, "Otra ocasión", 101L),
            ).isSuccess,
        )
        assertFalse(
            repository.redeem(
                RedemptionRequest("redemption-3", "r1", Player.Papi, "Sin saldo", 102L),
            ).isSuccess,
        )
    }

    private class FakeRewardDao : RewardDao {
        val catalog = MutableStateFlow<List<RewardEntity>>(emptyList())
        private val redemptions = MutableStateFlow<List<RedemptionEntity>>(emptyList())

        override suspend fun seedCatalog(rewards: List<RewardEntity>) {
            catalog.value = (rewards.associateBy { it.id } + catalog.value.associateBy { it.id }).values.toList()
        }

        override suspend fun insertReward(reward: RewardEntity) {
            require(catalog.value.none { it.id == reward.id })
            catalog.value += reward
        }

        override fun observeCatalog(): Flow<List<RewardEntity>> = catalog
        override suspend fun findReward(rewardId: String): RewardEntity? =
            catalog.value.firstOrNull { it.id == rewardId }
        override fun observeRedemptions(): Flow<List<RedemptionEntity>> = redemptions
        override suspend fun approvedPointsFor(player: String): Int = 1_000
        override suspend fun redeemedPointsFor(player: String): Int =
            redemptions.value.filter { it.player == player }.sumOf { it.rewardCost }
        override suspend fun insertRedemption(redemption: RedemptionEntity) {
            redemptions.value += redemption
        }
    }
}
