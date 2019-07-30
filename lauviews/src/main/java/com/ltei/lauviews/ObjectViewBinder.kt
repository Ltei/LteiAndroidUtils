package com.ltei.lauviews

import android.view.View
import com.ltei.ljubase.interfaces.ObjectBinder
import com.ltei.lauviews.recycler.ObjectViewHolder

interface ObjectViewBinder<T>: ObjectBinder<T> {
    val objectView: View

    fun toObjectViewHolder(): ObjectViewHolder<T> {
        return ObjectViewHolder.FromViewObjectBinder(this)
    }
}