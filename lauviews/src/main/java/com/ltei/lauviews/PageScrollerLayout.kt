package com.ltei.lauviews

import android.content.Context
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.ltei.ljubase.LIf

class PageScrollerLayout: LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs)

    var createPageView: ((page: Any) -> View)? = null

    fun showPages(pages: Iterable<*>) {
        removeAllViews()
        LIf.noNull(createPageView) { createPageView ->
            for (page in pages) {
                addView(createPageView.invoke(page!!))
            }
        }
    }

    fun appendPages(pages: Iterable<*>) {
        LIf.noNull(createPageView) { createPageView ->
            for (page in pages) {
                addView(createPageView.invoke(page!!))
            }
        }
    }

}