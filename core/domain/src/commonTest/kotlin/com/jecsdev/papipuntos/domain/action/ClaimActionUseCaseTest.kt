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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClaimActionUseCaseTest {

    @Test
    fun creates_a_trimmed_pending_request_for_papi_and_assigns_mami_as_approver() = runBlocking {
        val repository = RecordingActionRepository()
        val useCase = ClaimActionUseCase(
            repository = repository,
            idGenerator = ActionIdGenerator { "action-123" },
            timeProvider = TimeProvider { 1_725_000_000_000L },
        )

        val result = useCase(
            ClaimActionCommand(
                label = "  Lavaste los platos  ",
                points = 50,
                emoji = "🧽",
                beneficiary = Player.Papi,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(
            Action(
                id = "action-123",
                label = "Lavaste los platos",
                points = 50,
                emoji = "🧽",
                beneficiary = Player.Papi,
                approver = Player.Mami,
                status = ActionStatus.PENDING,
                createdAtEpochMillis = 1_725_000_000_000L,
            ),
            result.getOrThrow(),
        )
        assertEquals(result.getOrThrow(), repository.createdAction)
        assertNull(result.getOrThrow().resolvedAtEpochMillis)
        assertNull(result.getOrThrow().rejectionReason)
    }

    @Test
    fun assigns_papi_as_approver_for_a_request_benefiting_mami() = runBlocking {
        val repository = RecordingActionRepository()
        val useCase = ClaimActionUseCase(
            repository = repository,
            idGenerator = ActionIdGenerator { "action-456" },
            timeProvider = TimeProvider { 42L },
        )

        val result = useCase(
            ClaimActionCommand(
                label = "Le llevaste café",
                points = 30,
                emoji = "☕",
                beneficiary = Player.Mami,
            ),
        )

        assertEquals(Player.Papi, result.getOrThrow().approver)
        assertEquals(ActionStatus.PENDING, result.getOrThrow().status)
    }

    @Test
    fun rejects_invalid_requests_without_writing_to_the_repository() = runBlocking {
        val repository = RecordingActionRepository()
        val useCase = ClaimActionUseCase(
            repository = repository,
            idGenerator = ActionIdGenerator { "unused" },
            timeProvider = TimeProvider { 0L },
        )

        val blankLabel = useCase(ClaimActionCommand("   ", 10, "✨", Player.Papi))
        val zeroPoints = useCase(ClaimActionCommand("Ayudaste", 0, "✨", Player.Papi))
        val blankEmoji = useCase(ClaimActionCommand("Ayudaste", 10, "", Player.Papi))

        assertFalse(blankLabel.isSuccess)
        assertEquals("La acción necesita un nombre", blankLabel.exceptionOrNull()?.message)
        assertFalse(zeroPoints.isSuccess)
        assertEquals("Los puntos deben ser mayores que cero", zeroPoints.exceptionOrNull()?.message)
        assertFalse(blankEmoji.isSuccess)
        assertEquals("La acción necesita un emoji", blankEmoji.exceptionOrNull()?.message)
        assertNull(repository.createdAction)
    }

    @Test
    fun returns_the_repository_failure_without_reporting_a_successful_claim() = runBlocking {
        val failure = IllegalStateException("No se pudo guardar la acción")
        val repository = RecordingActionRepository(createResult = Result.failure(failure))
        val useCase = ClaimActionUseCase(
            repository = repository,
            idGenerator = ActionIdGenerator { "action-789" },
            timeProvider = TimeProvider { 0L },
        )

        val result = useCase(ClaimActionCommand("Hiciste la comida", 80, "🍳", Player.Papi))

        assertFalse(result.isSuccess)
        assertEquals(failure, result.exceptionOrNull())
        assertEquals("action-789", repository.createdAction?.id)
    }

    private class RecordingActionRepository(
        private val createResult: Result<Unit> = Result.success(Unit),
    ) : ActionRepository {
        var createdAction: Action? = null
            private set

        override suspend fun create(action: Action): Result<Unit> {
            createdAction = action
            return createResult
        }

        override fun observePendingFor(approver: Player): Flow<List<Action>> = emptyFlow()
    }
}
