package com.ltei.lauutils._deprecated

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout

@Deprecated("Use ViewUtils methods instead")
object LViews {

    fun setup(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT) {
        view.layoutParams = ViewGroup.LayoutParams(width, height)
    }

    fun setup(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, margins: Int = 0) {
        setup(
            view,
            width,
            height,
            margins,
            margins,
            margins,
            margins
        )
    }

    fun setup(
        view: View,
        width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT,
        marginStart: Int = 0, marginEnd: Int = 0,
        marginTop: Int = 0, marginBottom: Int = 0
    ) {
        val params = LinearLayout.LayoutParams(width, height)
        params.setMargins(marginStart, marginTop, marginEnd, marginBottom)
        view.layoutParams = params
    }

    fun dig(root: ViewGroup, vararg indices: Int): View {
        var view: View = root
        for (index in indices) {
            view = (view as ViewGroup).getChildAt(index)
        }
        return view
    }

    fun tryDig(root: ViewGroup, vararg indices: Int): View? {
        var view: View = root
        for (index in indices) {
            if (view is ViewGroup) {
                if (index < view.childCount) {
                    view = view.getChildAt(index)
                } else {
                    return null
                }
            } else {
                return null
            }
        }
        return view
    }

    fun forEachChild(root: ViewGroup, block: (View) -> Unit) {
        for (i in 0 until root.childCount) {
            root.getChildAt(i).let {
                block(it)
                if (it is ViewGroup) {
                    forEachChild(it, block)
                }
            }
        }
    }


    fun View.setDimensions(width: Int, height: Int) {
        layoutParams = ViewGroup.LayoutParams(width, height)
    }
    fun View.setPaddingStart(padding: Int) = setPadding(padding, paddingTop, paddingRight, paddingBottom)
    fun View.setPaddingBottom(padding: Int) = setPadding(paddingLeft, paddingTop, paddingRight, padding)
    fun View.setPaddingTop(padding: Int) = setPadding(paddingLeft, padding, paddingRight, paddingBottom)
    fun View.setPaddingVertical(padding: Int) = setPadding(paddingLeft, padding, paddingRight, padding)
    fun View.setPaddingHorizontal(padding: Int) = setPadding(padding, paddingTop, padding, paddingBottom)
    fun View.setPadding(padding: Int) = setPadding(padding, padding, padding, padding)

}