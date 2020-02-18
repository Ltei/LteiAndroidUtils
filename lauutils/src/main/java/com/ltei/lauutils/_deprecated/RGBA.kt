package com.ltei.lauutils._deprecated

import android.graphics.Color

@Deprecated("Useless")
interface RGBA : RGB {
    val a: Int

    override fun toColor(): Int = Color.argb(a, r, g, b)

    class Mutable(r: Int, g: Int, b: Int, override var a: Int): RGB.Mutable(r, g, b),
        RGBA {
        constructor(value: Int): this(Color.red(value), Color.green(value), Color.blue(value), Color.alpha(value))
    }

    companion object {
        fun new(r: Int, g: Int, b: Int, a: Int): RGBA =
            Mutable(r, g, b, a)
        fun new(value: Int): RGBA =
            Mutable(value)

        fun newMutable(r: Int, g: Int, b: Int, a: Int) =
            Mutable(r, g, b, a)
        fun newMutable(value: Int) = Mutable(value)
    }
}