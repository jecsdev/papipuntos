package com.jecsdev.papipuntos.data.action

import com.jecsdev.papipuntos.data.db.ActionDao
import com.jecsdev.papipuntos.data.db.ActionEntity
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed local-first implementation of [ActionRepository]. */
class RoomActionRepository(private val dao: ActionDao) : ActionRepository {
    override suspend fun create(action: Action): Result<Unit> = runCatching {
        dao.insert(action.toEntity())
    }

    override fun observePendingFor(approver: Player): Flow<List<Action>> =
        dao.observePendingFor(approver.name).map { actions -> actions.map(ActionEntity::toDomain) }
}

private fun Action.toEntity(): ActionEntity = ActionEntity(
    id = id,
    label = label,
    points = points,
    emoji = emoji,
    beneficiary = beneficiary.name,
    approver = approver.name,
    status = status.name,
    createdAtEpochMillis = createdAtEpochMillis,
    resolvedAtEpochMillis = resolvedAtEpochMillis,
    rejectionReason = rejectionReason,
)

private fun ActionEntity.toDomain(): Action = Action(
    id = id,
    label = label,
    points = points,
    emoji = emoji,
    beneficiary = Player.valueOf(beneficiary),
    approver = Player.valueOf(approver),
    status = ActionStatus.valueOf(status),
    createdAtEpochMillis = createdAtEpochMillis,
    resolvedAtEpochMillis = resolvedAtEpochMillis,
    rejectionReason = rejectionReason,
)
