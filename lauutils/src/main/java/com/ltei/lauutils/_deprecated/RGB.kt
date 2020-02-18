package com.ltei.lauutils._deprecated

import android.graphics.Color

@Deprecated("Useless")
interface RGB {
    val r: Int
    val g: Int
    val b: Int

    fun toColor() = Color.rgb(r, g, b)

    open class Mutable(override var r: Int, override var g: Int, override var b: Int):
        RGB {
        constructor(rgb: Int): this(Color.red(rgb), Color.green(rgb), Color.blue(rgb))
    }

    companion object {
        fun new(r: Int, g: Int, b: Int): RGB =
            Mutable(r, g, b)
        fun new(rgb: Int): RGB =
            Mutable(rgb)
    }
}