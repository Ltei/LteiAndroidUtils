package com.ltei.lauutils

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import android.util.Log

class UIThread {

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)

    fun post(block: () -> Unit) {
        if (Thread.currentThread() == looper.thread) {
            block.invoke()
        } else {
            handler.post(block)
        }
    }

}

object UIThread2 {

    inline fun post(crossinline block: () -> Unit) {
        val looper = Looper.getMainLooper()
        if (Thread.currentThread() == looper.thread) {
            block.invoke()
        } else {
            Handler(looper).post { block.invoke() }
        }
    }

}