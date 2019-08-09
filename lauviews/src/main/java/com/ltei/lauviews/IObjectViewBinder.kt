package com.ltei.lauviews

import android.view.View
import com.ltei.lauviews.recycler.ObjectViewHolder
import com.ltei.ljubase.interfaces.IObjectBinder

interface IObjectViewBinder<T> : IObjectBinder<T> {
    val objectView: View

    fun toObjectViewHolder(): ObjectViewHolder<T> {
        return ObjectViewHolder.FromViewObjectBinder(this)
    }
}