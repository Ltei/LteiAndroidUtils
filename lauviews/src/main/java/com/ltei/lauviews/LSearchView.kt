package com.ltei.lauviews

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.ltei.lauutils.LUnits
import com.ltei.ljubase.CompilationWarnings.UNUSED_PARAMETER

open class LSearchView : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    val leftIcon: ImageView = ImageView(context)
    val rightIcon: ImageView = ImageView(context)
    private val txtedSearch: EditText = EditText(context)

    var searchIconId: Int = android.R.drawable.ic_menu_search
        set(value) {
            field = value
            if (!isExpanded) {
                rightIcon.setImageResource(value)
            }
        }

    var closeIconId: Int = android.R.drawable.ic_menu_close_clear_cancel
        set(value) {
            field = value
            if (isExpanded) {
                rightIcon.setImageResource(value)
            }
        }

    private var lastText = ""
    private var isExpanded = false

    var onSearchChanged: ((before: String, after: String) -> Unit)? = null
    var onSearchViewOpened: (() -> Unit)? = null
    var onSearchViewClosed: (() -> Unit)? = null

    init {
        gravity = Gravity.CENTER_VERTICAL

        val margin = LUnits.dpToPx(resources, 12)

        val leftIconParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        leftIconParams.setMargins(0, margin, margin, margin)
        leftIconParams.addRule(ALIGN_PARENT_START)
        leftIconParams.addRule(CENTER_VERTICAL)
        leftIcon.setImageResource(android.R.drawable.ic_menu_search)
        leftIcon.setColorFilter(Color.argb(10, 255, 255, 255))
        leftIcon.layoutParams = leftIconParams
        leftIcon.visibility = View.GONE

        @Suppress("LeakingThis")
        addView(leftIcon)

        val searchEditTextParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        searchEditTextParams.addRule(END_OF, leftIcon.id)
        searchEditTextParams.addRule(CENTER_VERTICAL)
        txtedSearch.layoutParams = searchEditTextParams
        txtedSearch.addTextChangedListener(MEditTextWatcher())
        txtedSearch.visibility = View.GONE

        @Suppress("LeakingThis")
        addView(txtedSearch)

        val rightIconParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        rightIconParams.addRule(ALIGN_PARENT_END)
        rightIconParams.addRule(CENTER_VERTICAL)
        rightIcon.setImageResource(searchIconId)
        rightIcon.layoutParams = rightIconParams
        rightIcon.setOnClickListener(::onSearchIconClick)

        @Suppress("LeakingThis")
        addView(rightIcon)

        val focusChangeListener = MOnFocusChangeListener()
        onFocusChangeListener = focusChangeListener
        leftIcon.onFocusChangeListener = focusChangeListener
        rightIcon.onFocusChangeListener = focusChangeListener
        txtedSearch.onFocusChangeListener = focusChangeListener

        setOnClickListener(::onClick)
    }

    fun expandSearchView() {
        rightIcon.setImageResource(closeIconId)
        leftIcon.visibility = View.VISIBLE
        txtedSearch.visibility = View.VISIBLE
        onSearchViewOpened?.invoke()
        isExpanded = true
    }

    fun collapseSearchView() {
        rightIcon.setImageResource(searchIconId)
        leftIcon.visibility = View.GONE
        txtedSearch.visibility = View.GONE
        onSearchViewClosed?.invoke()
        isExpanded = false
    }

    private fun onClick(@Suppress(UNUSED_PARAMETER) v: View) {
        if (!isExpanded) expandSearchView()
    }

    private fun onSearchIconClick(@Suppress(UNUSED_PARAMETER) v: View) {
        if (isExpanded) collapseSearchView()
        else expandSearchView()
    }

    private inner class MOnFocusChangeListener : OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (!hasFocus) {
                collapseSearchView()
            }
        }
    }

    private inner class MEditTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val newText = it.toString()
                onSearchChanged?.invoke(lastText, newText)
                lastText = newText
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

}