package com.jecsdev.papipuntos.data.action

import com.jecsdev.papipuntos.domain.action.ActionIdGenerator
import com.jecsdev.papipuntos.domain.action.TimeProvider
import kotlin.random.Random
import kotlin.time.Clock

/** Generates collision-resistant identifiers locally without requiring network access. */
class RandomActionIdGenerator : ActionIdGenerator {
    override fun next(): String = buildString {
        repeat(16) {
            append(Random.nextInt(0, 256).toString(16).padStart(2, '0'))
        }
    }
}

/** Production time source used when creating a new request. */
class SystemTimeProvider : TimeProvider {
    override fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
