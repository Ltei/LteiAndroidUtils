package com.ltei.lauviews

import android.view.View
import com.ltei.ljubase.interfaces.ObjectBinder
import com.ltei.lauviews.recycler.ObjectViewHolder

interface ObjectViewBinder<T>: ObjectBinder<T> {
    val root: View

    fun toObjectViewHolder(): ObjectViewHolder<T> {
        return ObjectViewHolder.FromViewObjectBinder(this)
    }
}