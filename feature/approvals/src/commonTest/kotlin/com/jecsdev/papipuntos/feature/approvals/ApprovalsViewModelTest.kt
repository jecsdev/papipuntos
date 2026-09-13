package com.jecsdev.papipuntos.feature.approvals

import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.action.ResolveActionUseCase
import com.jecsdev.papipuntos.domain.action.TimeProvider
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
class ApprovalsViewModelTest {
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
    fun approving_a_request_removes_it_from_the_active_reviewers_inbox() = runTest {
        val repository = RecordingActionRepository()
        val viewModel = ApprovalsViewModel(
            activePlayer = Player.Mami,
            actionRepository = repository,
            resolveAction = ResolveActionUseCase(repository, TimeProvider { 2_000L }),
        )
        viewModel.uiState.first { it.actions.size == 1 }

        viewModel.decide(repository.pendingAction, com.jecsdev.papipuntos.domain.action.ActionDecision.Approve)
        val state = viewModel.uiState.first { it.actions.isEmpty() && it.resolvingActionId == null }

        assertTrue(state.errorMessage == null)
        assertEquals(ActionStatus.APPROVED, repository.resolvedAction?.status)
        assertEquals(2_000L, repository.resolvedAction?.resolvedAtEpochMillis)
    }

    @Test
    fun exposes_a_persistence_error_without_removing_the_request() = runTest {
        val repository = RecordingActionRepository(
            resolveResult = Result.failure(IllegalStateException("Base local no disponible")),
        )
        val viewModel = ApprovalsViewModel(
            activePlayer = Player.Mami,
            actionRepository = repository,
            resolveAction = ResolveActionUseCase(repository, TimeProvider { 2_000L }),
        )
        viewModel.uiState.first { it.actions.size == 1 }

        viewModel.decide(repository.pendingAction, com.jecsdev.papipuntos.domain.action.ActionDecision.Reject)
        val state = viewModel.uiState.first { it.errorMessage != null }

        assertEquals("Base local no disponible", state.errorMessage)
        assertEquals(1, state.actions.size)
    }

    private class RecordingActionRepository(
        private val resolveResult: Result<Boolean> = Result.success(true),
    ) : ActionRepository {
        val pendingAction = Action(
            id = "pending-action",
            label = "Lavaste los platos",
            points = 50,
            emoji = "🧽",
            beneficiary = Player.Papi,
            approver = Player.Mami,
            status = ActionStatus.PENDING,
            createdAtEpochMillis = 1_000L,
        )
        private val actions = MutableStateFlow(listOf(pendingAction))
        var resolvedAction: Action? = null
            private set

        override suspend fun create(action: Action): Result<Unit> = Result.success(Unit)

        override suspend fun findById(actionId: String): Action? = actions.value
            .firstOrNull { action -> action.id == actionId }

        override suspend fun resolveIfPending(action: Action): Result<Boolean> {
            resolvedAction = action
            if (resolveResult.isSuccess) {
                actions.value = actions.value.map { current ->
                    if (current.id == action.id) action else current
                }
            }
            return resolveResult
        }

        override fun observeAll(): Flow<List<Action>> = actions

        override fun observePendingFor(approver: Player): Flow<List<Action>> = actions.map { pending ->
            pending.filter { action ->
                action.approver == approver && action.status == ActionStatus.PENDING
            }
        }
    }
}
