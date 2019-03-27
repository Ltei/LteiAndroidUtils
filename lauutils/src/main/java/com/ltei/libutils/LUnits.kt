package com.ltei.lauutils

import android.content.res.Resources
import android.util.DisplayMetrics


object LUnits {

    private const val dayToMillisMult: Long = 24 * 60 * 60 * 1000
    fun dayToMillis(day: Int): Long {
        return day * dayToMillisMult
    }

    fun dpToPx(resources: Resources, dp: Int): Int {
        return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun pxToDp(resources: Resources, px: Int): Int {
        return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

}