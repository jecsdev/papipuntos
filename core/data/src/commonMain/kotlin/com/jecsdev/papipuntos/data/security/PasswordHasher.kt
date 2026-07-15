package com.jecsdev.papipuntos.data.security

import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.random.CryptoRand

/** A derived secret plus the random salt it was derived with, both hex-encoded. */
data class HashedSecret(val saltHex: String, val hashHex: String)

/** Hashes and verifies passwords and PINs. Implementations must never store plain text. */
interface PasswordHasher {
    /** Derives a hash for [raw] using a freshly generated random salt. */
    fun hash(raw: String): HashedSecret

    /** True if [raw] re-derives to [hashHex] under [saltHex]. Constant-time on the digest. */
    fun verify(raw: String, saltHex: String, hashHex: String): Boolean
}

/**
 * PBKDF2-HMAC-SHA256 hasher. A deliberately slow KDF matters here because the PIN is
 * only 4 digits (10k combinations): a fast hash like plain SHA-256 would be brute-forced
 * instantly if the database leaked. KotlinCrypto has no KDF module, so the standard PBKDF2
 * block loop is implemented on top of its HMAC-SHA256.
 */
class Pbkdf2PasswordHasher(
    private val iterations: Int = DEFAULT_ITERATIONS,
    private val saltLength: Int = DEFAULT_SALT_LENGTH,
    private val keyLength: Int = DEFAULT_KEY_LENGTH,
) : PasswordHasher {

    override fun hash(raw: String): HashedSecret {
        val salt = CryptoRand.Default.nextBytes(ByteArray(saltLength))
        val derived = pbkdf2HmacSha256(raw.encodeToByteArray(), salt, iterations, keyLength)
        return HashedSecret(saltHex = salt.toHex(), hashHex = derived.toHex())
    }

    override fun verify(raw: String, saltHex: String, hashHex: String): Boolean {
        val salt = saltHex.hexToBytes()
        val expected = hashHex.hexToBytes()
        val derived = pbkdf2HmacSha256(raw.encodeToByteArray(), salt, iterations, expected.size)
        return derived.constantTimeEquals(expected)
    }

    private companion object {
        const val DEFAULT_ITERATIONS = 100_000
        const val DEFAULT_SALT_LENGTH = 16
        const val DEFAULT_KEY_LENGTH = 32
    }
}

private const val H_LEN = 32 // HMAC-SHA256 output size in bytes

/**
 * RFC 2898 PBKDF2 with HMAC-SHA256 as the PRF. Internal so tests can check it against the
 * published RFC test vectors — a round-trip alone would pass even a consistently-wrong loop.
 */
internal fun pbkdf2HmacSha256(password: ByteArray, salt: ByteArray, iterations: Int, dkLen: Int): ByteArray {
    val prf = HmacSHA256(password)
    val blocks = (dkLen + H_LEN - 1) / H_LEN
    val output = ByteArray(blocks * H_LEN)

    for (block in 1..blocks) {
        // U1 = PRF(salt || INT_32_BE(block)); T = U1
        prf.update(salt)
        prf.update(block.toBigEndianBytes())
        var u = prf.doFinal()
        val t = u.copyOf()
        // U2..Uc = PRF(previous U); T = T xor U2 xor ... xor Uc
        for (round in 2..iterations) {
            u = prf.doFinal(u)
            for (i in t.indices) t[i] = (t[i].toInt() xor u[i].toInt()).toByte()
        }
        t.copyInto(output, (block - 1) * H_LEN)
    }
    return output.copyOf(dkLen)
}

private fun Int.toBigEndianBytes(): ByteArray = byteArrayOf(
    (this ushr 24).toByte(),
    (this ushr 16).toByte(),
    (this ushr 8).toByte(),
    this.toByte(),
)

internal fun ByteArray.toHex(): String =
    joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }

private fun String.hexToBytes(): ByteArray =
    ByteArray(length / 2) { substring(it * 2, it * 2 + 2).toInt(16).toByte() }

/** Compares all bytes without short-circuiting so timing does not leak how much matched. */
private fun ByteArray.constantTimeEquals(other: ByteArray): Boolean {
    if (size != other.size) return false
    var diff = 0
    for (i in indices) diff = diff or (this[i].toInt() xor other[i].toInt())
    return diff == 0
}
