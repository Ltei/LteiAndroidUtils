package com.ltei.laustate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.ltei.ljubase.Logger

abstract class State(
    val inAnimationId: Int = android.R.anim.fade_in,
    val outAnimationId: Int = android.R.anim.fade_out
) : Fragment() {

    internal var mStateManager: StateManager? = null
        set(value) {
            if (field != null) throw IllegalStateException()
            field = value
        }
    val stateManager get() = mStateManager!!

    private val logger = Logger(State::class.java, name = javaClass.simpleName)
    var stateView: View? = null

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logger.debug()
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(context, inAnimationId))
    }

    override fun onResume() {
        logger.debug()
        stateManager.onStateOnResumeListeners.forEach { it.invoke(this) }
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

    open fun shouldRemoveFromBackstack(newState: State): Boolean = true

}