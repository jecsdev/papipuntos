package com.jecsdev.papipuntos.feature.scoreboard

import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry

/** Flat, render-ready state for the scoreboard screen. */
data class ScoreboardUiState(
    val coupleNames: String,
    val papiName: String,
    val mamiName: String,
    val papiAvatar: String,
    val mamiAvatar: String,
    val papiPoints: Int,
    val mamiPoints: Int,
    val goalPoints: Int,
    val recentActions: List<ActionEntry>,
)
