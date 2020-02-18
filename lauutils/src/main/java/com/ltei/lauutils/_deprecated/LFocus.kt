package com.ltei.lauutils._deprecated

import android.view.View

@Deprecated("Has been integrated into std")
object LFocus {

    fun clear(view: View) {
        view.isFocusableInTouchMode = false
        view.isFocusable = false
        view.isFocusableInTouchMode = true
        view.isFocusable = true
    }

}