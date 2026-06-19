package com.jecsdev.papipuntos.feature.scoreboard

import com.jecsdev.papipuntos.feature.scoreboard.model.ActionEntry
import com.jecsdev.papipuntos.feature.scoreboard.model.Player

/**
 * Hardcoded sample state mirroring the mockup. Shared by the ViewModel and the
 * `@Preview`s — this port carries no real business logic, only example data.
 */
object ScoreboardSampleData {
    private val actions = listOf(
        ActionEntry("h1", "Lavaste los platos", 50, Player.Papi, "Hoy · 20:14", "🧽"),
        ActionEntry("h2", "Le llevaste un café", 30, Player.Mami, "Hoy · 08:02", "☕"),
        ActionEntry("h3", "Planificaste una cita", 100, Player.Papi, "Ayer · 19:30", "💕"),
        ActionEntry("h4", "Le diste un abrazo inesperado", 25, Player.Mami, "Ayer · 15:10", "🤗"),
        ActionEntry("h5", "Hiciste la comida", 80, Player.Papi, "Lun · 13:45", "🍳"),
        ActionEntry("h6", "Sacaste al perro", 20, Player.Mami, "Lun · 07:20", "🐶"),
        ActionEntry("h7", "Le diste un masaje", 100, Player.Papi, "Dom · 22:00", "💆"),
    )

    val state = ScoreboardUiState(
        coupleNames = "Sofía & Mateo",
        papiName = "Mateo",
        mamiName = "Sofía",
        papiAvatar = "👨🏻",
        mamiAvatar = "👩🏻",
        papiPoints = 730,
        mamiPoints = 690,
        goalPoints = 1000,
        recentActions = actions,
    )
}
