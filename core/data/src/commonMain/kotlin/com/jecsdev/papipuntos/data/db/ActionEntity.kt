package com.jecsdev.papipuntos.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persistent representation of a request for points. */
@Entity(tableName = "action_claim")
data class ActionEntity(
    @PrimaryKey val id: String,
    val label: String,
    val points: Int,
    val emoji: String,
    val beneficiary: String,
    val approver: String,
    val status: String,
    val createdAtEpochMillis: Long,
    val resolvedAtEpochMillis: Long? = null,
    val rejectionReason: String? = null,
)
