package com.ltei.lauutils

import android.content.Context
import android.widget.Toast

object LToast {

    @JvmOverloads
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }

}