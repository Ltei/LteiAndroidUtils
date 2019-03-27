package com.ltei.lauutils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE


object LClipboard {

    fun copy(context: Context, text: String) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("label", text)
        clipboard!!.setPrimaryClip(clip)
    }

}