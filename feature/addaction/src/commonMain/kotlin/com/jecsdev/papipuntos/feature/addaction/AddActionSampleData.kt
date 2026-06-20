package com.jecsdev.papipuntos.feature.addaction

import com.jecsdev.papipuntos.feature.addaction.model.SuggestedAction

/** Hard-coded suggestions mirroring the mockup's `SUGGESTED` list (preview data). */
object AddActionSampleData {
    val suggestions: List<SuggestedAction> = listOf(
        SuggestedAction("a1", "Lavaste los platos", 50, "🧽"),
        SuggestedAction("a2", "Le llevaste café", 30, "☕"),
        SuggestedAction("a3", "Hiciste la comida", 80, "🍳"),
        SuggestedAction("a4", "Ordenaste algo que sabías que le gusta", 40, "🥡"),
        SuggestedAction("a5", "Le diste un masaje", 100, "💆"),
        SuggestedAction("a6", "La escuchaste sin interrumpir", 30, "👂"),
        SuggestedAction("a7", "La ayudaste sin que te lo pidiera", 40, "🤝"),
        SuggestedAction("a8", "Planificaste una cita", 100, "💕"),
        SuggestedAction("a9", "Sacaste al perro", 20, "🐶"),
        SuggestedAction("a10", "Fuiste a buscarla al trabajo", 75, "🚗"),
    )
}
