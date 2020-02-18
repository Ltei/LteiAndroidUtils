package com.ltei.lauviews.recycler

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ltei.lauviews.interfaces.IObjectViewBinder

abstract class ObjectViewHolder<T>(objectView: View) : RecyclerView.ViewHolder(objectView), IObjectViewBinder<T> {

    override val objectView: View get() = itemView

    abstract class Simple<T>(objectView: View) : ObjectViewHolder<T>(objectView) {
        override val boundObject: T? = null
    }

    open class FromViewObjectBinder<T>(val objectViewBinder: IObjectViewBinder<T>) :
        ObjectViewHolder<T>(objectViewBinder.objectView) {
        override val boundObject: T? get() = objectViewBinder.boundObject

        override fun bind(obj: T) {
            objectViewBinder.bind(obj)
        }
    }

    class Text(context: Context, val onClick: ((String) -> Unit)? = null) :
        ObjectViewHolder<String>(TextView(context)) {

        override var boundObject: String? = null

        override fun bind(obj: String) {
            (itemView as TextView).text = obj
            onClick?.let { onClick ->
                itemView.setOnClickListener { onClick(obj) }
            }
            boundObject = obj
        }
    }

}