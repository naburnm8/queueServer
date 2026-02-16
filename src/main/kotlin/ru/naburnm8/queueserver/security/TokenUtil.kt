package ru.naburnm8.queueserver.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object TokenUtil {
    private val rnd = SecureRandom()

    fun newRefreshTokenValue(): String {
        val bytes = ByteArray(32)
        rnd.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun sha256Hex(value: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}