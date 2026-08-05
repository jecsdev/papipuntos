package com.jecsdev.papipuntos.data.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordHasherTest {

    // RFC 7914 section 11 publishes PBKDF2-HMAC-SHA-256 vectors. Matching the exact output
    // proves the hand-written block loop is correct (a consistently-wrong loop would still
    // pass a round-trip, so the vectors are the real check).

    @Test
    fun pbkdf2_matches_rfc7914_vector_c1() {
        val dk = pbkdf2HmacSha256("passwd".encodeToByteArray(), "salt".encodeToByteArray(), 1, 64)
        assertEquals(
            "55ac046e56e3089fec1691c22544b605f94185216dde0465e68b9d57c20dacbc" +
                "49ca9cccf179b645991664b39d77ef317c71b845b1e30bd509112041d3a19783",
            dk.toHex(),
        )
    }

    @Test
    fun pbkdf2_matches_rfc7914_vector_c80000() {
        val dk = pbkdf2HmacSha256("Password".encodeToByteArray(), "NaCl".encodeToByteArray(), 80000, 64)
        assertEquals(
            "4ddcd8f60b98be21830cee5ef22701f9641a4418d04c0414aeff08876b34ab56" +
                "a1d425a1225833549adb841b51c9b3176a272bdebba1d078478f62b397f33c8d",
            dk.toHex(),
        )
    }

    @Test
    fun hasher_round_trips_and_rejects_wrong_pin() {
        val hasher = Pbkdf2PasswordHasher(iterations = 1_000)
        val secret = hasher.hash("1234")
        assertTrue(hasher.verify("1234", secret.saltHex, secret.hashHex))
        assertFalse(hasher.verify("0000", secret.saltHex, secret.hashHex))
    }

    @Test
    fun hasher_uses_a_fresh_random_salt_each_time() {
        val hasher = Pbkdf2PasswordHasher(iterations = 1_000)
        val first = hasher.hash("1234")
        val second = hasher.hash("1234")
        assertFalse(first.saltHex == second.saltHex, "each hash must use a fresh salt")
    }
}
