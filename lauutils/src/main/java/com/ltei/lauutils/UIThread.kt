package com.ltei.lauutils

import android.os.Handler
import android.os.Looper

object UIThread {
    private val handler = Handler(Looper.getMainLooper())
    fun run(block: () -> Unit) = handler.post(block)
}