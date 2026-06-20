package com.jecsdev.papipuntos.feature.scoreboard.model

import com.jecsdev.papipuntos.model.Player

/** A single logged action shown in the "Últimas acciones" list. */
data class ActionEntry(
    val id: String,
    val label: String,
    val points: Int,
    val player: Player,
    val timestamp: String,
    val emoji: String,
)
