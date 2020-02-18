package com.ltei.lauutils._deprecated

import android.content.res.Resources
import android.util.DisplayMetrics


@Deprecated("Use UnitUtils methods instead")
object LUnits {

    private const val dayToMillisMult: Long = 24 * 60 * 60 * 1000
    fun dayToMillis(day: Int): Long {
        return day * dayToMillisMult
    }

    fun dpToPx(resources: Resources, dp: Int): Int =
        Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))

    fun pxToDp(resources: Resources, px: Int): Int {
        return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

}