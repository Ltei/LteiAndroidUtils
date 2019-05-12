package com.ltei.lauviews

import android.animation.Animator
import android.view.animation.Animation
import android.view.animation.RotateAnimation


object LAnimations {

    fun setListeners(
            animation: Animation,
            onRepeat: (() -> Unit)? = null,
            onStart: (() -> Unit)? = null,
            onEnd: (() -> Unit)? = null
    ) {
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                onRepeat?.invoke()
            }

            override fun onAnimationStart(animation: Animation?) {
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animation?) {
                onEnd?.invoke()
            }
        })
    }

    fun setListeners(
            animation: Animator,
            onRepeat: (() -> Unit)? = null,
            onStart: ((isReverse: Boolean) -> Unit)? = null,
            onEnd: ((isReverse: Boolean) -> Unit)? = null,
            onCancel: (() -> Unit)? = null
    ) {
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                onRepeat?.invoke()
            }

            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                onStart?.invoke(isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {
                onStart?.invoke(false)
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                onEnd?.invoke(isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd?.invoke(false)
            }

            override fun onAnimationCancel(animation: Animator?) {
                onCancel?.invoke()
            }
        })
    }

    fun buildRotation(
            fromDegrees: Float,
            toDegrees: Float,
            duration: Long = 1000,
            fillAfter: Boolean = false,
            pivotXValue: Float = 0.5f,
            pivotYValue: Float = 0.5f
    ): RotateAnimation {
        val result = RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, pivotXValue, Animation.RELATIVE_TO_SELF, pivotYValue)
        result.duration = duration
        result.fillAfter = fillAfter
        return result
    }

    /*fun buildColorChange(context: Context,
                         colorStart: Int,
                         colorEnd: Int,
                         duration: Long,
                         onAnimationEndListener: OnAnimationEndListener = object: OnAnimationEndListener {
                             override fun onAnimationEnd() {}
                         }): Animation {

        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.duration = duration

        val hsv: FloatArray = FloatArray(3)
        var runColor: Int
        val hue = 0
        // Transition color
        hsv[1] = 1f
        hsv[2] = 1f

        val colorStart = Color.parseColor(String.format("#%06X", 0xFFFFFF and colorStart))
        val colorEnd = Color.parseColor(String.format("#%06X", 0xFFFFFF and colorEnd))

        anim.addUpdateListener { animation ->
            hsv[0] = 360 * animation.animatedFraction

            runColor = Color.HSVToColor(hsv)
            yourView.setBackgroundColor(runColor)
        }

        anim.repeatCount = Animation.INFINITE

        anim.start()
    }*/

}