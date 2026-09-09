package com.jecsdev.papipuntos.feature.addaction

import com.jecsdev.papipuntos.domain.action.ActionIdGenerator
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.action.ClaimActionUseCase
import com.jecsdev.papipuntos.domain.action.TimeProvider
import com.jecsdev.papipuntos.feature.addaction.model.SuggestedAction
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddActionViewModelTest {
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
    fun submits_the_selected_suggestion_and_emits_a_navigation_event() = runTest {
        val repository = RecordingActionRepository()
        val viewModel = AddActionViewModel(claimActionUseCase(repository))
        val suggestion = SuggestedAction("coffee", "Le llevaste café", 30, "☕")
        val event = async { viewModel.events.first() }

        viewModel.selectTarget(Player.Mami)
        viewModel.submit(suggestion)
        advanceUntilIdle()

        assertEquals(AddActionEvent.Submitted(Player.Mami, suggestion), event.await())
        assertEquals(Player.Mami, repository.createdAction?.beneficiary)
        assertEquals(Player.Papi, repository.createdAction?.approver)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun keeps_the_form_open_and_exposes_an_error_when_persistence_fails() = runTest {
        val repository = RecordingActionRepository(Result.failure(IllegalStateException("Base local no disponible")))
        val viewModel = AddActionViewModel(claimActionUseCase(repository))

        viewModel.submit(SuggestedAction("meal", "Hiciste la comida", 80, "🍳"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals("Base local no disponible", viewModel.uiState.value.errorMessage)
        assertTrue(repository.createdAction != null)
    }

    @Test
    fun updates_search_and_clears_a_previous_error_when_the_target_changes() = runTest {
        val repository = RecordingActionRepository(Result.failure(IllegalStateException("Falló")))
        val viewModel = AddActionViewModel(claimActionUseCase(repository))

        viewModel.updateQuery("café")
        viewModel.submit(SuggestedAction("coffee", "Le llevaste café", 30, "☕"))
        advanceUntilIdle()
        viewModel.selectTarget(Player.Mami)

        assertEquals("café", viewModel.uiState.value.query)
        assertEquals(Player.Mami, viewModel.uiState.value.target)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun claimActionUseCase(repository: ActionRepository): ClaimActionUseCase = ClaimActionUseCase(
        repository = repository,
        idGenerator = ActionIdGenerator { "action-id" },
        timeProvider = TimeProvider { 1L },
    )

    private class RecordingActionRepository(private val result: Result<Unit> = Result.success(Unit)) :
        ActionRepository {
        var createdAction: Action? = null
            private set

        override suspend fun create(action: Action): Result<Unit> {
            createdAction = action
            return result
        }

        override fun observePendingFor(approver: Player): Flow<List<Action>> = emptyFlow()
    }
}
