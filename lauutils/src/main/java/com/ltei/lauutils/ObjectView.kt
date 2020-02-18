package com.ltei.lauutils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ltei.ljubase.interfaces.IObjectBinder

interface ObjectView<T> : IObjectBinder<T> {
    val view: View
    override var boundObject: T?
    fun updateViewFromObject()
}

fun <T> ObjectView<T>.toViewHolder() = ObjectViewHolder(this)

class ObjectViewHolder<T>(
    val objectView: ObjectView<T>
) : RecyclerView.ViewHolder(objectView.view), ObjectView<T> by objectView

