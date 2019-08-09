package com.ltei.lauviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout

class ButtonBar : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
            context: Context, buttons: Array<Pair<String, OnClickListener?>>,
            tint: Int = Color.WHITE, textColor: Int = Color.BLACK
    ) : super(context) {
        setButtons(buttons, tint, textColor)
    }

    init {
        orientation = HORIZONTAL
    }

    fun setButtons(
            buttons: Array<Pair<String, OnClickListener?>>,
            tint: Int = Color.WHITE,
            textColor: Int = Color.BLACK
    ) {
        for ((title, onClick) in buttons) {
            val buttonView = Button(context)
            buttonView.layoutParams = LayoutParams(0, WRAP_CONTENT, 1f)
            buttonView.text = title
            buttonView.setTextColor(textColor)
            buttonView.background.setTint(tint)
            onClick?.let { buttonView.setOnClickListener(it) }
            addView(buttonView)
        }
    }

    fun getButton(idx: Int): Button {
        return getChildAt(idx) as Button
    }

}

@Deprecated("Use ButtonBar instead")
class OldButtonBar : LinearLayout { // TODO REMOVE

    constructor(
            context: Context,
            buttons: Array<Pair<String, () -> Unit>>,
            tint: Int = Color.WHITE,
            textColor: Int = Color.BLACK
    ) : super(context) {
        init(context, buttons, tint, textColor)
    }

    constructor(context: Context, attrs: AttributeSet, buttons: Array<Pair<String, () -> Unit>>) : super(
            context,
            attrs
    ) {
        init(context, buttons)
    }

    private fun init(
            context: Context,
            buttons: Array<Pair<String, () -> Unit>>,
            tint: Int = Color.WHITE,
            textColor: Int = Color.BLACK
    ) {
        orientation = HORIZONTAL

        for (button in buttons) {
            val buttonView = Button(context)
            buttonView.layoutParams = LayoutParams(0, WRAP_CONTENT, 1f)
            buttonView.text = button.first
            buttonView.setTextColor(textColor)
            buttonView.background.setTint(tint)
            buttonView.setOnClickListener { button.second() }
            addView(buttonView)
        }
    }

    fun getButton(idx: Int): Button {
        return getChildAt(idx) as Button
    }

}