package com.jecsdev.papipuntos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Data access for point requests. */
@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(action: ActionEntity)

    @Query("SELECT * FROM action_claim WHERE id = :actionId LIMIT 1")
    suspend fun findById(actionId: String): ActionEntity?

    @Query(
        "UPDATE action_claim SET " +
            "status = :status, " +
            "resolvedAtEpochMillis = :resolvedAtEpochMillis, " +
            "rejectionReason = :rejectionReason " +
            "WHERE id = :id AND approver = :approver AND status = 'PENDING'",
    )
    suspend fun resolveIfPending(
        id: String,
        approver: String,
        status: String,
        resolvedAtEpochMillis: Long,
        rejectionReason: String?,
    ): Int

    @Query("SELECT * FROM action_claim ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<ActionEntity>>

    @Query(
        "SELECT * FROM action_claim " +
            "WHERE approver = :approver AND status = 'PENDING' " +
            "ORDER BY createdAtEpochMillis ASC",
    )
    fun observePendingFor(approver: String): Flow<List<ActionEntity>>
}
