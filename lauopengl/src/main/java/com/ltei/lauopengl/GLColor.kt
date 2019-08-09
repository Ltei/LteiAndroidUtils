package com.ltei.lauopengl

import android.graphics.Color

class GLColor(r: Float, g: Float, b: Float, a: Float = 1f) {

    companion object {
        fun black(): GLColor = GLColor(0f, 0f, 0f, 1f)
        fun white(): GLColor = GLColor(1f, 1f, 1f, 1f)
        fun red(): GLColor = GLColor(1f, 0f, 0f, 1f)
        fun green(): GLColor = GLColor(0f, 1f, 0f, 1f)
        fun blue(): GLColor = GLColor(0f, 0f, 1f, 1f)
        fun transparent(): GLColor = GLColor(0f, 0f, 0f, 0f)
        fun rgba(r: Float, g: Float, b: Float, a: Float): GLColor = GLColor(r, g, b, a)

        fun fromColor(color: Int): GLColor {
            val r = Color.red(color).toFloat()
            val g = Color.green(color).toFloat()
            val b = Color.blue(color).toFloat()
            val a = Color.alpha(color).toFloat()
            return GLColor(r / 255f, g / 255f, b / 255f, a / 255f)
        }
    }

    var rgba: FloatArray

    init {
        if (r < 0f || r > 1f) throw IllegalArgumentException()
        if (g < 0f || g > 1f) throw IllegalArgumentException()
        if (b < 0f || b > 1f) throw IllegalArgumentException()
        if (a < 0f || a > 1f) throw IllegalArgumentException()
        rgba = floatArrayOf(r, g, b, a)
    }


    fun red(): Float = rgba[0]
    fun green(): Float = rgba[1]
    fun blue(): Float = rgba[2]
    fun alpha(): Float = rgba[3]

}