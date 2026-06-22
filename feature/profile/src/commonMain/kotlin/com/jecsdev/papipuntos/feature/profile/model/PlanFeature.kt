package com.jecsdev.papipuntos.feature.profile.model

/** A capability line in a plan card. [included] toggles the check + strikethrough. */
data class PlanFeature(
    val label: String,
    val included: Boolean,
)
