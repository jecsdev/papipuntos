package com.jecsdev.papipuntos.feature.profile

import com.jecsdev.papipuntos.feature.profile.model.Achievement

/**
 * Hardcoded sample data mirroring the mockup's profile. This port carries no real
 * business logic, only example content for the screen/preview.
 */
object ProfileSampleData {
    const val PAPI_NAME = "Mateo"
    const val MAMI_NAME = "Sofía"
    const val PAPI_AVATAR = "👨🏻"
    const val MAMI_AVATAR = "👩🏻"
    const val PAPI_POINTS = 730
    const val MAMI_POINTS = 690
    const val STREAK_DAYS = 12

    val achievements = listOf(
        Achievement("🏆", "Primera semana"),
        Achievement("💯", "100 acciones"),
        Achievement("💆", "Maestro del masaje"),
        Achievement("🍳", "Chef de la casa"),
        Achievement("💕", "Cita perfecta"),
        Achievement("🌙", "Detalle nocturno"),
    )
}
