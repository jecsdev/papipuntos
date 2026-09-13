package com.jecsdev.papipuntos.model

/** A point request that must be reviewed by the other profile before it affects a balance. */
data class Action(
    val id: String,
    val label: String,
    val points: Int,
    val emoji: String,
    val beneficiary: Player,
    val approver: Player,
    val status: ActionStatus,
    val createdAtEpochMillis: Long,
    val resolvedAtEpochMillis: Long? = null,
    val rejectionReason: String? = null,
)

/** The lifecycle of a point request. Only approved actions can affect a profile's balance. */
enum class ActionStatus {
    PENDING,
    APPROVED,
    REJECTED,
}
