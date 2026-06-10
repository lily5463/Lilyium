package com.lily.lilyiumplayer.util

import java.security.MessageDigest

object HashUtil {
    fun generateSalt(): String {
        val allowedChars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
