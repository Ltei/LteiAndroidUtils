package com.ltei.lauutils

import android.view.View

object LFocus {

    fun clear(view: View) {
        view.isFocusableInTouchMode = false
        view.isFocusable = false
        view.isFocusableInTouchMode = true
        view.isFocusable = true
    }

}