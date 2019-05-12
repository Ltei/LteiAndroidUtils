package com.ltei.lauutils

import android.graphics.Color

object LColors {

    class RGB(val r: Int, val g: Int, val b: Int)

    fun toRGB(value: Int): RGB {
        return RGB(Color.red(value), Color.green(value), Color.blue(value))
    }

}