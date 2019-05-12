package com.ltei.laustate

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import com.ltei.ljubase.LLog

abstract class State(
        val inAnimationId: Int = android.R.anim.fade_in,
        val outAnimationId: Int = android.R.anim.fade_out
) : Fragment() {

    var stateView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LLog.info(this.javaClass)
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(context, inAnimationId))
    }

    override fun onResume() {
        LLog.info(this.javaClass)
        super.onResume()
    }

    override fun onPause() {
        LLog.info(this.javaClass)
        super.onPause()
    }

    open fun onBackPressed(): Boolean {
        LLog.info(this.javaClass)
        return false
    }

    /** Used by StateManager, for backstack management */
    abstract fun shouldRemoveFromBackstack(newState: State): Boolean

}