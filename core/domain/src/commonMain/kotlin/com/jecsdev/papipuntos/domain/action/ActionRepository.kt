package com.jecsdev.papipuntos.domain.action

import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.Flow

/** Persistence boundary for action requests. Implementations belong to the data layer. */
interface ActionRepository {
    suspend fun create(action: Action): Result<Unit>

    /** Looks up one request before a domain use case decides whether it can be resolved. */
    suspend fun findById(actionId: String): Action?

    /** Resolves a request only if it is still pending and belongs to its assigned approver. */
    suspend fun resolveIfPending(action: Action): Result<Boolean>

    /** All requests, newest first, for the couple's scoreboard and history. */
    fun observeAll(): Flow<List<Action>>

    /** Requests that [approver] still needs to approve or reject. */
    fun observePendingFor(approver: Player): Flow<List<Action>>
}
