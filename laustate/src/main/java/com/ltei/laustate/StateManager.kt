package com.ltei.laustate

import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.ltei.lauviews.LAnimations
import com.ltei.ljubase.Logger

/**
 * Manages fragment holder
 * Only one instance of any Class<State> is allowed in backstack, pushing a
 * new instance will result on the deletion of the last instances.
 */
class StateManager(
    private val parent: FragmentActivity,
    private val stateLayoutId: Int
) {

    private val logger = Logger(this)
    private var mBackstack = ArrayList<State>()

    val backstack: List<State> get() = mBackstack
    val currentState: State get() = mBackstack.last()
    val isRootStateOnTop: Boolean get() = mBackstack.size == 1


    fun setState(state: State) {
        if (mBackstack.size == 0) {
            parent.supportFragmentManager.beginTransaction()
                .replace(stateLayoutId, state)
                .addToBackStack(null)
                .commit()
        } else {
            tryStartChangeAnimation {
                parent.supportFragmentManager.beginTransaction()
                    .replace(stateLayoutId, state)
                    .addToBackStack(null)
                    .commit()
            }

            if (currentState.shouldRemoveFromBackstack(state)) {
                mBackstack.remove(currentState)
            }
            mBackstack.removeAll { it.javaClass == state.javaClass }
        }
        mBackstack.add(state)
    }

    fun backToHome() {
        if (mBackstack.size > 1) {
            for (i in 1 until (mBackstack.size - 1)) {
                mBackstack.removeAt(1)
            }
            popState()
        }
    }

    fun popState(): Boolean {
        return if (mBackstack.size > 1) {
            tryStartChangeAnimation {
                mBackstack.removeAt(mBackstack.size - 1)
                val newState = currentState // == mBackstack.last()
                parent.supportFragmentManager.beginTransaction()
                    .replace(stateLayoutId, newState)
                    .addToBackStack(null)
                    .commit()
            }
            true
        } else {
            false
        }
    }

    inline fun <reified T : State> applyToStates(block: (T) -> Unit) {
        for (state in backstack) {
            if (state is T) {
                block(state)
            }
        }
    }

    /**
     * Call it from parent activity
     */
    fun onBackPressed() {
        if (!currentState.onBackPressed())
            popState()
    }

    private fun tryStartChangeAnimation(onAnimationEnd: () -> Unit) {
        val currentStateView = currentState.view
        if (currentStateView == null) {
            logger.debug("tryStartChangeAnimation : Aborted since state is already changing ($currentStateView)")
            return
        }
        val animation = AnimationUtils.loadAnimation(parent.applicationContext, currentState.outAnimationId)
        LAnimations.setListeners(animation, onEnd = onAnimationEnd)
        currentStateView.startAnimation(animation)
    }

}