package com.jecsdev.papipuntos.feature.scoreboard.model

/** A single logged action shown in the "Últimas acciones" list. */
data class ActionEntry(
    val id: String,
    val label: String,
    val points: Int,
    val player: Player,
    val timestamp: String,
    val emoji: String,
)
