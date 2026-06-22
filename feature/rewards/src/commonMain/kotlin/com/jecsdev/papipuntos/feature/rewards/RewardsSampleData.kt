package com.jecsdev.papipuntos.feature.rewards

import com.jecsdev.papipuntos.feature.rewards.model.Reward

/**
 * Hardcoded sample data mirroring the mockup's `REWARDS` and `REASONS`. This port
 * carries no real business logic, only example content for the screens/previews.
 */
object RewardsSampleData {
    val rewards = listOf(
        Reward("r1", "Noche de película elegida por mí", 500, "🎬"),
        Reward("r2", "Masaje de 30 minutos", 600, "💆"),
        Reward("r3", "Desayuno en la cama", 400, "☕"),
        Reward("r4", "Un día sin quejarme", 300, "😎"),
        Reward("r5", "Escoger dónde comer", 350, "🍔"),
        Reward("r6", "Dormir una hora más", 250, "🛏️"),
        Reward("r7", "Noche de gaming sin reclamos", 450, "🎮"),
        Reward("r8", "Salida de compras sin límite de tiempo", 700, "🛍️"),
    )

    // Filter options for the "Canjear puntos" dropdown.
    val filters = listOf(
        "Todas las recompensas",
        "Hasta 400 pts",
        "Entre 400 y 600 pts",
        "Premium 600+",
    )

    val reasons = listOf(
        "Porque me lo merezco",
        "Tuve un día difícil",
        "Semana pesada",
        "Celebración especial",
        "Sin razón, solo quiero que me consientan",
    )
}
