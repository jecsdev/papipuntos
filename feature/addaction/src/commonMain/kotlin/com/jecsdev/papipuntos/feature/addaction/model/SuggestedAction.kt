package com.jecsdev.papipuntos.feature.addaction.model

/** A pickable suggestion shown in the "Agregar acción" list. */
data class SuggestedAction(
    val id: String,
    val label: String,
    val points: Int,
    val emoji: String,
)
