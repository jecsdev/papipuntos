package com.jecsdev.papipuntos.data.auth

import com.jecsdev.papipuntos.model.AuthState
import com.jecsdev.papipuntos.model.NewProfile
import com.jecsdev.papipuntos.model.Player
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InMemoryAuthRepositoryTest {
    @Test
    fun profile_switch_returns_to_the_pin_protected_selector_without_logging_out() = runBlocking {
        val repository = InMemoryAuthRepository()
        repository.signUp("pareja@example.com", "password").getOrThrow()
        repository.saveProfiles(
            papi = NewProfile(Player.Papi, "Mateo", "👨🏻", "1234"),
            mami = NewProfile(Player.Mami, "Sofía", "👩🏻", "5678"),
        ).getOrThrow()
        repository.unlockProfile(Player.Papi, "1234").getOrThrow()

        repository.requestProfileSwitch().getOrThrow()

        val state = assertIs<AuthState.ProfileSelection>(repository.state.value)
        assertEquals(listOf(Player.Papi, Player.Mami), state.profiles.map { it.player })
        assertTrue(repository.unlockProfile(Player.Mami, "5678").isSuccess)
        assertEquals(Player.Mami, assertIs<AuthState.Active>(repository.state.value).current.player)
    }
}
