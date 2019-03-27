package com.ltei.lauutils

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout

object LViews {

    fun setup(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT) {
        view.layoutParams = ViewGroup.LayoutParams(width, height)
    }

    fun setup(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, margins: Int = 0) {
        setup(view, width, height, margins, margins, margins, margins)
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

    fun forEachChild(root: ViewGroup, forEach: (View) -> Unit) {
        for (i in 0 until root.childCount) {
            root.getChildAt(i).let {
                forEach(it)
                if (it is ViewGroup) {
                    forEachChild(it, forEach)
                }
            }
        }
    }

    fun setVisibility(view: View, visibility: Int) {
        if (view.visibility != visibility) {
            view.visibility = visibility
        }
    }

    fun setVisible(view: View) {
        setVisibility(view, View.VISIBLE)
    }

    fun setGone(view: View) {
        setVisibility(view, View.GONE)
    }

    fun setPaddingStart(view: View, padding: Int) {
        view.setPadding(padding, view.paddingTop, view.paddingRight, view.paddingBottom)
    }

    fun setPaddingBottom(view: View, padding: Int) {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, padding)
    }

    fun setPaddingVertical(view: View, padding: Int) {
        view.setPadding(view.paddingLeft, padding, view.paddingRight, padding)
    }

    fun setPaddingHorizontal(view: View, padding: Int) {
        view.setPadding(padding, view.paddingTop, padding, view.paddingBottom)
    }

    fun setPadding(view: View, padding: Int) {
        view.setPadding(padding, padding, padding, padding)
    }

}