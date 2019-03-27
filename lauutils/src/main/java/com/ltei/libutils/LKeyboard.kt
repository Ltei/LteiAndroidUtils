package com.ltei.lauutils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


object LKeyboard {

    fun hide(activity: Activity) { // tryRun todo replace Activity with Context
        if (activity.currentFocus != null) {
            activity.window.currentFocus?.windowToken?.let { windowToken ->
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    fun openWithFocus(context: Context, editText: EditText) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.requestFocus()
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

}