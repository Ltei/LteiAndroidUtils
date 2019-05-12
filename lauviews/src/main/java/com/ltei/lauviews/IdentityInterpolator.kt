package com.ltei.lauviews

import android.view.animation.Interpolator

class IdentityInterpolator : Interpolator {

    var reverse = false

    override fun getInterpolation(paramFloat: Float): Float {
        return if (reverse) 1f - paramFloat else paramFloat
    }
}