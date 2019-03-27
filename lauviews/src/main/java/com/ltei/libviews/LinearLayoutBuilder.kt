package com.ltei.lauviews

import android.content.Context
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

open class LinearLayoutBuilder(val root: LinearLayout) {

    constructor(context: Context, padding: Int? = null, orientation: Int = LinearLayout.VERTICAL) : this(
            LinearLayout(
                    context
            )
    ) {
        root.layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        padding?.let { root.setPadding(it, it, it, it) }
        orientation.let { root.orientation = it }
    }

    var isBuilt = false
        protected set

    fun view(view: View) {
        assertIsNotBuild()
        root.addView(view)
    }

    fun space(size: Int) {
        assertIsNotBuild()
        val view = Space(root.context)
        view.layoutParams = ViewGroup.LayoutParams(size, size)
        view(view)
    }

    fun text(
            text: String,
            textSize: Float? = null,
            color: Int? = null,
            lineCount: Int = 1,
            maxLines: Int = 1,
            paddingTop: Int = 0,
            paddingBottom: Int = 0
    ): TextView {

        assertIsNotBuild()
        val view = TextView(root.context)
        view.gravity = Gravity.CENTER
        view.text = text
        if (textSize != null) view.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                textSize / root.context.resources.displayMetrics.scaledDensity
        )
        if (color != null) view.setTextColor(color)
        view.setLines(lineCount)
        view.maxLines = maxLines
        view.setPadding(0, paddingTop, 0, paddingBottom)
        root.addView(view)
        return view
    }

    fun editableText(
            text: String? = null, hint: String? = null, type: Int = InputType.TYPE_CLASS_TEXT,
            lineCount: Int? = null, maxLines: Int? = null, gravity: Int = Gravity.CENTER
    ): EditText {

        assertIsNotBuild()
        val view = EditText(root.context)
        view.gravity = gravity
        text?.let { view.setText(it) }
        hint?.let { view.hint = it }
        view.inputType = type
//        view.setBackgroundColor(Color.TRANSPARENT) TODO Wtf was that
        lineCount?.let { view.setLines(it) }
        maxLines?.let { view.maxLines = it }

        root.addView(view)
        return view
    }

    fun input(hint: String = "", type: Int = InputType.TYPE_CLASS_TEXT, lineCount: Int = 1): EditText {
        assertIsNotBuild()
        val view = EditText(root.context)
        view.gravity = Gravity.CENTER
        view.hint = hint
        view.inputType = type
        view.setLines(lineCount)
        root.addView(view)
        return view
    }

    fun button(
            text: String,
            textColor: Int? = null,
            backgroundColor: Int? = null,
            onClickListener: View.OnClickListener? = null
    ): Button {
        assertIsNotBuild()
        val view = Button(root.context)
        view.text = text
        if (textColor != null) view.setTextColor(textColor)
        if (backgroundColor != null) view.background.setTint(backgroundColor)
        onClickListener?.let { view.setOnClickListener(it) }
        root.addView(view)
        return view
    }

    /*fun datePicker(): DatePicker {
        assertIsNotBuild()
        val view = dialog.layoutInflater.inflate(R.layout.view_datepicker_spinner, null) as DatePicker
        root.addView(view)
        return view
    }*/

    fun build(): LinearLayout {
        assertIsNotBuild()
        isBuilt = true
        return root
    }

    protected fun assertIsNotBuild() {
        if (isBuilt) {
            throw IllegalStateException("Builder cannot be used after build() call")
        }
    }

}