package com.jecsdev.papipuntos.domain.action

import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResolveActionUseCaseTest {
    @Test
    fun approves_a_pending_action_when_the_assigned_profile_reviews_it() = runBlocking {
        val repository = RecordingActionRepository(pendingAction())
        val useCase = ResolveActionUseCase(repository, TimeProvider { 2_000L })

        val result = useCase(
            ResolveActionCommand(
                actionId = "action-1",
                reviewer = Player.Mami,
                decision = ActionDecision.Approve,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(ActionStatus.APPROVED, result.getOrThrow().status)
        assertEquals(2_000L, result.getOrThrow().resolvedAtEpochMillis)
        assertEquals(result.getOrThrow(), repository.resolvedAction)
    }

    @Test
    fun rejects_a_pending_action_and_preserves_a_trimmed_reason() = runBlocking {
        val repository = RecordingActionRepository(pendingAction())
        val useCase = ResolveActionUseCase(repository, TimeProvider { 2_000L })

        val result = useCase(
            ResolveActionCommand(
                actionId = "action-1",
                reviewer = Player.Mami,
                decision = ActionDecision.Reject,
                rejectionReason = "  Ya lo habíamos hecho  ",
            ),
        )

        assertEquals(ActionStatus.REJECTED, result.getOrThrow().status)
        assertEquals("Ya lo habíamos hecho", result.getOrThrow().rejectionReason)
    }

    @Test
    fun refuses_a_review_from_the_beneficiary() = runBlocking {
        val repository = RecordingActionRepository(pendingAction())
        val useCase = ResolveActionUseCase(repository, TimeProvider { 2_000L })

        val result = useCase(
            ResolveActionCommand("action-1", Player.Papi, ActionDecision.Approve),
        )

        assertFalse(result.isSuccess)
        assertEquals("No puedes revisar esta acción", result.exceptionOrNull()?.message)
        assertEquals(null, repository.resolvedAction)
    }

    @Test
    fun refuses_a_second_decision() = runBlocking {
        val repository = RecordingActionRepository(pendingAction(status = ActionStatus.APPROVED))
        val useCase = ResolveActionUseCase(repository, TimeProvider { 2_000L })

        val result = useCase(
            ResolveActionCommand("action-1", Player.Mami, ActionDecision.Reject),
        )

        assertFalse(result.isSuccess)
        assertEquals("Esta acción ya fue resuelta", result.exceptionOrNull()?.message)
    }

    private fun pendingAction(status: ActionStatus = ActionStatus.PENDING): Action = Action(
        id = "action-1",
        label = "Lavaste los platos",
        points = 50,
        emoji = "🧽",
        beneficiary = Player.Papi,
        approver = Player.Mami,
        status = status,
        createdAtEpochMillis = 1_000L,
    )

    private class RecordingActionRepository(private val action: Action?) : ActionRepository {
        var resolvedAction: Action? = null
            private set

        override suspend fun create(action: Action): Result<Unit> = Result.success(Unit)

        override suspend fun findById(actionId: String): Action? = action?.takeIf { it.id == actionId }

        override suspend fun resolveIfPending(action: Action): Result<Boolean> {
            resolvedAction = action
            return Result.success(true)
        }

        override fun observeAll(): Flow<List<Action>> = emptyFlow()

        override fun observePendingFor(approver: Player): Flow<List<Action>> = emptyFlow()
    }
}
