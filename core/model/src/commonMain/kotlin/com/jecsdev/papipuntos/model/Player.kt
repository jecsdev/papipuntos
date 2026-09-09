package com.jecsdev.papipuntos.model

/** One of the two profiles in the couple. Points are never shared between them. */
enum class Player {
    Papi,
    Mami,
}

/** Returns the profile that must review a request created for this profile. */
fun Player.other(): Player = when (this) {
    Player.Papi -> Player.Mami
    Player.Mami -> Player.Papi
}
