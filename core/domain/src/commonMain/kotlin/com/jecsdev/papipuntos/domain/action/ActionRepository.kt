package com.jecsdev.papipuntos.domain.action

import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.Flow

/** Persistence boundary for action requests. Implementations belong to the data layer. */
interface ActionRepository {
    suspend fun create(action: Action): Result<Unit>

    /** Requests that [approver] still needs to approve or reject. */
    fun observePendingFor(approver: Player): Flow<List<Action>>
}
