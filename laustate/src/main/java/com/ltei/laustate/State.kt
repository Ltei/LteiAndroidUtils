package com.ltei.laustate

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.ltei.ljubase.Logger

abstract class State(
    val inAnimationId: Int = android.R.anim.fade_in,
    val outAnimationId: Int = android.R.anim.fade_out
) : Fragment() {

    private val logger = Logger(State::class.java)
    var stateView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.debug()
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(context, inAnimationId))
    }

    override fun onResume() {
        logger.debug()
        super.onResume()
    }

    override fun onPause() {
        logger.debug()
        super.onPause()
    }

    open fun onBackPressed(): Boolean {
        logger.debug()
        return false
    }

    /** Used by StateManager, for backstack management */
    abstract fun shouldRemoveFromBackstack(newState: State): Boolean

}