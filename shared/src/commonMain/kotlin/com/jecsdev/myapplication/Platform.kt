package com.jecsdev.myapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform