package com.jecsdev.papipuntos.feature.profile

import com.jecsdev.papipuntos.feature.profile.model.PlanFeature

/** Billing cycles for the premium plan, with the copy shown in the price block. */
enum class BillingCycle(val label: String, val price: String, val period: String) {
    Monthly("Mensual", "$3.99", "/mes"),
    Yearly("Anual · -37%", "$29.99", "/año"),
}

/** Hardcoded plan content mirroring the mockup's `free` / `premium` lists. */
object PlansSampleData {
    val free = listOf(
        PlanFeature("Registrar acciones básicas", included = true),
        PlanFeature("Hasta 5 recompensas", included = true),
        PlanFeature("Historial de 30 días", included = true),
        PlanFeature("Logros y rachas premium", included = false),
        PlanFeature("Acciones y recompensas ilimitadas", included = false),
    )

    val premium = listOf(
        PlanFeature("Acciones ilimitadas", included = true),
        PlanFeature("Recompensas ilimitadas y personalizadas", included = true),
        PlanFeature("Historial completo + estadísticas", included = true),
        PlanFeature("Logros, rachas y multiplicadores", included = true),
        PlanFeature("Temas y avatares exclusivos", included = true),
    )
}
