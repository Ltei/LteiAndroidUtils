package com.ltei.lauviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout

open class LProgressBar : LinearLayout {

    private var mProgress: Float = 0f
    private var mMaxProgress: Float = 0f

    val progress get() = mProgress
    val maxProgress get() = mMaxProgress

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

    fun updateProgress(progress: Float = mProgress, maxProgress: Float = mMaxProgress) {
        mProgress = progress
        mMaxProgress = maxProgress
        update()
    }

    fun setProgressTint(tint: ColorStateList) {
        viewLeft.backgroundTintList = tint
    }

    fun setProgressBackgroundTint(tint: ColorStateList) {
        viewRight.backgroundTintList = tint
    }

    fun setProgressZeroTint(tint: ColorStateList) {
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
            viewLeft.visibility = View.GONE
            viewRight.visibility = View.GONE
            viewZero.visibility = View.VISIBLE
        } else { // TODO
            viewLeft.visibility = View.VISIBLE
            viewRight.visibility = View.VISIBLE
            viewZero.visibility = View.GONE
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