package com.ltei.lauutils.recycler

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ltei.lauutils.ObjectViewHolder


abstract class SimpleObjectViewAdapter<T> : RecyclerView.Adapter<ObjectViewHolder<T>>() {
    var data: List<T> = listOf()
        private set

    abstract fun createObjectView(context: Context): ObjectViewHolder<T>
    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean
    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    open fun formatData(data: List<T>): List<T> = data

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder<T> {
        return createObjectView(parent.context)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder<T>, position: Int) {
        if (position in data.indices) {
            val item = data[position]
            holder.boundObject = item
            holder.updateViewFromObject()
        }
    }

    fun setData(data: List<T>, notify: Boolean = true) {
        val formattedData = formatData(data)
        if (notify) {
            val callback = DiffUtilCallback(this.data, formattedData)
            val result = DiffUtil.calculateDiff(callback)
            this.data = formattedData
            result.dispatchUpdatesTo(this)
        } else {
            this.data = formattedData
        }
    }

    // Inner

    private inner class DiffUtilCallback(
        oldList: List<T>,
        newList: List<T>
    ) : SimpleDiffUtilCallback<T>(oldList, newList) {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            this@SimpleObjectViewAdapter.areItemsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            this@SimpleObjectViewAdapter.areContentsTheSame(oldItem, newItem)
    }

}