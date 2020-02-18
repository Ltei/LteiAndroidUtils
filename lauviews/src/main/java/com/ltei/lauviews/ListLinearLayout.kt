package com.ltei.lauviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.ltei.lauutils.LUnits
import com.ltei.lauviews.interfaces.IObjectViewBinder

open class ListLinearLayout : LinearLayout {

    class ItemView(val item: Any, val view: View)

    private var list: List<*>? = null
    var itemViews: List<ItemView> = listOf()
        private set
    var viewCreator: ViewCreator? = null
    var separatorSize = 0
    var separatorColor: Int? = null
    var separatorAtStart = false
    var separatorAtEnd = false

    interface ViewCreator {
        fun createView(item: Any, itemPosition: Int): View
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs)


    fun setObservedList(list: List<*>) {
        this.list = list
        notifyArrayChange()
    }

    fun addItem(item: Any) {
        val viewCreator = viewCreator
        val index = list!!.size

        if (viewCreator != null) {
            if (separatorSize != 0 && separatorAtStart && !separatorAtEnd) addView(createSeparator())
            addView(viewCreator.createView(item, index))
            if (separatorSize != 0 && separatorAtEnd) addView(createSeparator())
        }
    }

    fun notifyArrayChange() {
        removeAllViews()
        val itemViews = ArrayList<ItemView>()
        if (viewCreator != null) {
            if (separatorSize != 0 && separatorAtStart) addView(createSeparator())
            for (item in list!!.withIndex()) {
                val view = viewCreator!!.createView(item.value!!, item.index)
                itemViews.add(ItemView(item.value!!, view))
                addView(view)
                if (separatorSize != 0 && item.index < list!!.size - 1) {
                    addView(createSeparator())
                }
            }
            if (separatorSize != 0 && separatorAtEnd) addView(createSeparator())
        }
        this.itemViews = itemViews
    }

    private fun createSeparator(): View {
        val separator = View(context)
        val separatorParams = if (orientation == VERTICAL) {
            ViewGroup.LayoutParams(MATCH_PARENT, separatorSize)
        } else {
            ViewGroup.LayoutParams(separatorSize, MATCH_PARENT)
        }
        separator.layoutParams = separatorParams
        separatorColor?.let { separator.setBackgroundColor(it) }
        return separator
    }

    fun setupAll(
        list: List<Any>,
        orientation: Int = LinearLayout.VERTICAL,
        separatorColor: Int? = null,
        separatorSize: Int = 1,
        separatorAtStart: Boolean = false,
        separatorAtEnd: Boolean = false,
        viewText: ((Any) -> String)? = null,
        onClick: ((Any, Int) -> Unit)? = null,
        viewHeight: Int = LUnits.dpToPx(context.resources, 50),
        viewColor: Int? = null
    ) {
        setupAll(
            list, orientation, separatorColor, separatorSize, separatorAtStart, separatorAtEnd,
            getDefaultViewCreator(viewText, onClick, viewHeight, color = viewColor)
        )
    }

    fun setupAll(
        list: List<Any>,
        orientation: Int = LinearLayout.VERTICAL,
        separatorColor: Int? = null,
        separatorSize: Int = 1,
        separatorAtStart: Boolean = false,
        separatorAtEnd: Boolean = false,
        viewCreator: ViewCreator
    ) {
        this.orientation = orientation
        this.separatorColor = separatorColor
        this.separatorSize = separatorSize
        this.separatorAtStart = separatorAtStart
        this.separatorAtEnd = separatorAtEnd
        this.viewCreator = viewCreator
        setObservedList(list)
    }

    fun <T> setViewCreator(creator: (Context) -> IObjectViewBinder<T>) {
        this.viewCreator = object : ViewCreator {
            override fun createView(item: Any, itemPosition: Int): View {
                val view = creator(context)
                view.bind(item as T)
                return view.objectView
            }
        }
    }

    fun setDefaultViewCreator(
        viewText: ((Any) -> String)? = null,
        onClick: ((Any, Int) -> Unit)? = null,
        height: Int = LUnits.dpToPx(context.resources, 50),
        padding: Int = 0,
        gravity: Int = Gravity.CENTER,
        color: Int? = null
    ) {
        this.viewCreator = getDefaultViewCreator(viewText, onClick, height, padding, gravity, color)
    }

    private fun getDefaultViewCreator(
        viewText: ((Any) -> String)? = null,
        onClick: ((Any, Int) -> Unit)? = null,
        height: Int = LUnits.dpToPx(context.resources, 50),
        padding: Int = 0,
        gravity: Int = Gravity.CENTER,
        color: Int? = null
    ): ViewCreator {
        return object : ViewCreator {
            override fun createView(item: Any, itemPosition: Int): View {
                val result = TextView(context)
                result.setPadding(padding, padding, padding, padding)
                result.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, height)
                result.gravity = gravity
                color?.let { result.setBackgroundColor(it) }

                result.text = if (viewText != null) {
                    viewText(item)
                } else {
                    item.toString()
                }

                if (onClick != null) {
                    result.setOnClickListener { onClick(item, itemPosition) }
                }

                return result
            }
        }
    }

}