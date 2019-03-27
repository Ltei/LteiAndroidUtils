package com.ltei.lauutils

import android.util.Base64
import kotlin.experimental.xor


object LEncryption {

    fun decrypt(message: String, salt: String): String {
        return xor(String(Base64.decode(message, 0)), salt)
    }

    fun encrypt(message: String, salt: String): String {
        return String(Base64.encode(xor(message, salt).toByteArray(), 0))
    }

    /** Encrypts or decrypts a base-64 string using a XOR cipher. */
    private fun xor(message: String, salt: String): String {
        val m = message.toByteArray()
        val ml = m.size

        val s = salt.toByteArray()
        val sl = s.size

        val res = CharArray(ml)
        for (i in 0 until ml) {
            res[i] = (m[i] xor s[i % sl]).toChar()
        }
        return String(res)
    }

}