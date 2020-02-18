package com.ltei.lauutils.misc

import android.view.View
import android.view.ViewGroup

fun View.setPaddingStart(padding: Int) = setPadding(padding, paddingTop, paddingRight, paddingBottom)
fun View.setPaddingBottom(padding: Int) = setPadding(paddingLeft, paddingTop, paddingRight, padding)
fun View.setPaddingTop(padding: Int) = setPadding(paddingLeft, padding, paddingRight, paddingBottom)
fun View.setPaddingVertical(padding: Int) = setPadding(paddingLeft, padding, paddingRight, padding)
fun View.setPaddingHorizontal(padding: Int) = setPadding(padding, paddingTop, padding, paddingBottom)
fun View.setPadding(padding: Int) = setPadding(padding, padding, padding, padding)

fun ViewGroup.dig(vararg indices: Int): View {
    var view: View = this
    for (index in indices) {
        view = (view as ViewGroup).getChildAt(index)
    }
    return view
}

fun ViewGroup.tryDig(vararg indices: Int): View? {
    var view: View = this
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