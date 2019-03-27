package com.ltei.lauviews.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ltei.ljubase.interfaces.ObjectBinder
import com.ltei.lauviews.ObjectViewBinder

abstract class ObjectViewHolder<T>(objectView: View) : RecyclerView.ViewHolder(objectView), ObjectBinder<T> {

    class FromViewObjectBinder<T>(val objectViewBinder: ObjectViewBinder<T>): ObjectViewHolder<T>(objectViewBinder.root) {
        override fun bind(obj: T) {
            objectViewBinder.bind(obj)
        }

        override fun getBoundObject(): T? {
            return objectViewBinder.getBoundObject()
        }
    }

    class Text(context: Context, val onClick: ((String) -> Unit)? = null) : ObjectViewHolder<String>(TextView(context)) {

        private var text: String? = null

        override fun bind(obj: String) {
            (itemView as TextView).text = obj
            onClick?.let { onClick ->
                itemView.setOnClickListener { onClick(obj) }
            }
            text = obj
        }

        override fun getBoundObject(): String? {
            return text
        }
    }

}