package com.ltei.lauutils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager


class KeyboardManager constructor(
    private val activity: Activity
) : ViewTreeObserver.OnGlobalLayoutListener {

    companion object {
        private const val MAGIC_NUMBER = 200
    }

    private val rootView: View = activity.window.decorView.rootView
    private var mIsKeyboardVisible: Boolean? = null
    val isKeyboardVisible: Boolean get() = mIsKeyboardVisible!!
    private val mScreenDensity: Float

    private val mListeners = ArrayList<SoftKeyboardToggleListener>()


    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = activity.resources.displayMetrics.density
    }

    override fun onGlobalLayout() {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = rootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (mIsKeyboardVisible == null || isVisible != mIsKeyboardVisible) {
            mIsKeyboardVisible = isVisible
            mListeners.forEach { it.onToggleSoftKeyboard(isVisible) }
        }
    }

    /**
     * Add a new keyboard listener
     * @param listener callback
     */
    fun addKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
        removeKeyboardToggleListener(listener)
        mListeners.add(listener)
    }

    /**
     * Remove a registered listener
     * @param listener [SoftKeyboardToggleListener]
     */
    fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener)
        }
    }

    /**
     * Remove all registered keyboard listeners
     */
    fun removeAllKeyboardToggleListeners() {
        mListeners.clear()
    }

    /**
     * Manually toggle soft keyboard visibility
     * @param context calling context
     */
    fun toggleKeyboardVisibility(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    /**
     * Force closes the soft keyboard
     */
    fun forceCloseKeyboard() {
        if (activity.currentFocus != null) {
            activity.window.currentFocus?.windowToken?.let { windowToken ->
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            }
        }
        //(activeView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(activeView.windowToken, 0)
    }

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

}