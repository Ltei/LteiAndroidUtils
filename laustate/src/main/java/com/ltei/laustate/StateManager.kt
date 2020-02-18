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

    private var mBackstack = ArrayList<State>()

    val onStateOnResumeListeners = mutableListOf<(State) -> Unit>()

    val backstack: List<State> get() = mBackstack
    val currentState: State get() = mBackstack.last()
    val isRootStateOnTop: Boolean get() = mBackstack.size == 1


    fun onRecreateActivity() {
        parent.supportFragmentManager.beginTransaction()
            .replace(stateLayoutId, currentState)
            .addToBackStack(null)
            .commit()
    }

    fun setState(state: State) {
        state.mStateManager = this
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

    /**
     * Call it from parent activity
     */
    fun onBackPressed(): Boolean {
        if (currentState.onBackPressed()) return true
        if (popState()) return true
        return false
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

    companion object {
        private val logger = Logger(StateManager::class.java)
    }

}