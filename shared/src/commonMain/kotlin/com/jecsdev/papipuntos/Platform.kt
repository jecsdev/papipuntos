package com.jecsdev.papipuntos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform