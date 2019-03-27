package com.ltei.lauviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import com.ltei.lauutils.LViews

class LProgressBar : LinearLayout {

    private var mProgress: Float = 0f
    private var mMaxProgress: Float = 0f

    private val viewLeft: View
    private val viewRight: View
    private val viewZero: View

    constructor(context: Context) : super(context) {
        viewLeft = View(context)
        viewRight = View(context)
        viewZero = View(context)
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        viewLeft = View(context)
        viewRight = View(context)
        viewZero = View(context)
        setup()
    }

    fun pgbSetProgress(progress: Float = mProgress, maxProgress: Float = mMaxProgress) {
        mProgress = progress
        mMaxProgress = maxProgress
        update()
    }

    fun pgbGetProgress(): Float {
        return mProgress
    }

    fun pgbGetMaxProgress(): Float {
        return mMaxProgress
    }

    fun pgbSetProgressTint(tint: ColorStateList) {
        viewLeft.backgroundTintList = tint
    }

    fun pgbSetBackgroundTint(tint: ColorStateList) {
        viewRight.backgroundTintList = tint
    }

    fun pgbSetZeroTint(tint: ColorStateList) {
        viewZero.backgroundTintList = tint
    }

    private fun setup() {
        viewLeft.setBackgroundResource(R.drawable.bg_progress_bar_left)
        viewRight.backgroundTintList = resources.getColorStateList(android.R.color.holo_green_light)
        addView(viewLeft)
        viewRight.setBackgroundResource(R.drawable.bg_progress_bar_right)
        viewRight.backgroundTintList = resources.getColorStateList(android.R.color.holo_red_light)
        addView(viewRight)
        viewZero.setBackgroundResource(R.drawable.bg_progress_bar_zero)
        viewZero.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray)
        viewZero.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        addView(viewZero)
        update()
    }

    private fun update() {
        if (mMaxProgress <= 0f || mProgress > mMaxProgress) {
            LViews.setVisibility(viewLeft, View.GONE)
            LViews.setVisibility(viewRight, View.GONE)
            LViews.setVisibility(viewZero, View.VISIBLE)
        } else { // TODO
            LViews.setVisibility(viewLeft, View.VISIBLE)
            LViews.setVisibility(viewRight, View.VISIBLE)
            LViews.setVisibility(viewZero, View.GONE)
            viewLeft.layoutParams = getParams(mProgress)
            viewRight.layoutParams = getParams(mMaxProgress - mProgress)
        }
    }

    private fun getParams(weight: Float): LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(0, MATCH_PARENT)
        params.weight = weight
        return params
    }

}