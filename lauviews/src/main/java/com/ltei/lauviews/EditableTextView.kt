package com.ltei.lauviews

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

/**
 * Custom edit text view to handle some common usage patterns
 */
class EditableTextView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) :
    AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {

    private open class BaseAnimatorListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }


    var mBehavior: Behavior? = null
        set(value) {
            field = value
            mBehavior!!.onAttach(this)
        }

    private var mFocused = false

    var value = ""
        private set

    init {
        initListeners()
        mBehavior = object : Behavior {
            private val mPattern: String? = null

            private fun format(text: String): String {
                return String.format(mPattern!!, text)
            }

            override fun onAttach(view: EditableTextView) {}

            override fun onFocusIn(direction: Int, value: String, text: String): String? {
                return if (text == value) null else value
            }

            override fun onTextChanged(s: Editable): String? {
                return null
            }

            override fun onFocusOut(direction: Int, value: String?, text: String): String? {
                val isFalsy = value == null || value.isEmpty()
                val finalValue = if (isFalsy) value else format(value!!)
                return if (text == finalValue) null else finalValue
            }
        }
    }

    /**
     * Set up listeners
     */
    private fun initListeners() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {
                if (mFocused) value = s.toString()
                if (mBehavior != null) setDisplayText(mBehavior!!.onTextChanged(s))
            }
        })
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, rect: Rect?) {
        mFocused = focused
        if (mBehavior != null) {
            val displayText: String?
            val currValue = value
            val currText = text.toString()

            displayText = if (focused) {
                mBehavior!!.onFocusIn(direction, currValue, currText)
            } else {
                mBehavior!!.onFocusOut(direction, currValue, currText)
            }
            setDisplayText(displayText)
        }
        super.onFocusChanged(focused, direction, rect)
    }

    /**
     * Set the view display text if given string is not null, which
     * will trigger [Behavior.onTextChanged] after text change
     *
     * @param text the text string to set
     */
    fun setDisplayText(text: String?) {
        val animateHalfDuration = 48
        val hiddenTextColor = Color.argb(0, 0, 0, 0)
        val currentTextColor = currentTextColor
        if (text != null && text != getText().toString()) {
            val animator = animateTextColor(animateHalfDuration.toLong(), currentTextColor, hiddenTextColor)
            animator.addListener(object : BaseAnimatorListener() {
                override fun onAnimationEnd(animation: Animator) {
                    setText(text)
                    setSelection(text.length)
                    animateTextColor(animateHalfDuration.toLong(), hiddenTextColor, currentTextColor)
                }
            })
        }
    }

    /**
     * Animate the textColor property of this instance
     *
     * @param duration the length of the transition in milliseconds
     * @param colors the list of colors to switch through
     * @return the animator used
     */
    fun animateTextColor(duration: Long, vararg colors: Int): Animator {
        val animator = ObjectAnimator.ofObject(this, "textColor", ArgbEvaluator(), arrayOf(colors) as Array<*>)
        animator.duration = duration
        animator.start()
        return animator
    }

    /**
     * The edit text view behavior
     */
    interface Behavior {
        /**
         * Called when behaviour is attached to [EditableTextView]
         *
         * @param view the swappable image view attached
         */
        fun onAttach(view: EditableTextView)

        /**
         * Called when the edit text view gains focus.
         *
         * @param direction the direction from [.onFocusChanged]
         * @param value the current value from [.getValue]
         * @param text the current text from [.getText]
         * @return the text to display or null to ignore
         */
        fun onFocusIn(direction: Int, value: String, text: String): String?

        /**
         * Called when changes have been made to the edit text view.
         * Returning a value or calling [EditableTextView.setText]
         * will call this method again recursively.
         * Therefore to avoid getting stuck in an infinite loop,
         * return NULL when no change is to be made to display text.
         *
         * @param s the editable from [TextWatcher.afterTextChanged]
         * @return the text to display or null to ignore
         */
        fun onTextChanged(s: Editable): String?

        /**
         * Called when the edit text view drops focus.
         *
         * @param direction the direction from [.onFocusChanged]
         * @param value the current value from [.getValue]
         * @param text the current text from [.getText]
         * @return the text to display or null to ignore
         */
        fun onFocusOut(direction: Int, value: String?, text: String): String?
    }
}