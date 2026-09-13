package com.jecsdev.papipuntos.feature.scoreboard

import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import com.jecsdev.papipuntos.model.other
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreboardViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun derives_balances_from_approved_actions_and_keeps_pending_actions_in_history() = runTest {
        val repository = RecordingActionRepository(
            listOf(
                action(id = "pending", beneficiary = Player.Papi, points = 50, status = ActionStatus.PENDING),
                action(id = "approved", beneficiary = Player.Mami, points = 30, status = ActionStatus.APPROVED),
            ),
        )
        val viewModel = ScoreboardViewModel(repository, profiles())

        val state = viewModel.uiState.first { it.mamiPoints == 30 }
        advanceUntilIdle()

        assertEquals("Mateo & Sofía", state.coupleNames)
        assertEquals("👨🏻", state.papiAvatar)
        assertEquals("👩🏻", state.mamiAvatar)
        assertEquals(0, state.papiPoints)
        assertEquals(30, state.mamiPoints)
        assertEquals(2, state.recentActions.size)
        assertEquals("Pendiente", state.recentActions.first().timestamp)
        assertEquals("Aprobada", state.recentActions.last().timestamp)
    }

    private fun profiles(): List<Profile> = listOf(
        Profile(Player.Papi, "Mateo", "👨🏻"),
        Profile(Player.Mami, "Sofía", "👩🏻"),
    )

    private fun action(
        id: String,
        beneficiary: Player,
        points: Int,
        status: ActionStatus,
    ): Action = Action(
        id = id,
        label = "Acción $id",
        points = points,
        emoji = "✨",
        beneficiary = beneficiary,
        approver = beneficiary.other(),
        status = status,
        createdAtEpochMillis = 1L,
    )

    private class RecordingActionRepository(initialActions: List<Action>) : ActionRepository {
        private val actions = MutableStateFlow(initialActions)

        override suspend fun create(action: Action): Result<Unit> = Result.success(Unit)

        override suspend fun findById(actionId: String): Action? = null

        override suspend fun resolveIfPending(action: Action): Result<Boolean> = Result.success(false)

        override fun observeAll(): Flow<List<Action>> = actions

        override fun observePendingFor(approver: Player): Flow<List<Action>> = emptyFlow()
    }
}
