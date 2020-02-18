package com.ltei.lauutils.recycler

import androidx.recyclerview.widget.DiffUtil

abstract class SimpleDiffUtilCallback<T>(
    open val oldList: List<T>,
    open val newList: List<T>
): DiffUtil.Callback() {
    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean
    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    final override fun getOldListSize(): Int = oldList.size
    final override fun getNewListSize(): Int = newList.size

    final override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areItemsTheSame(oldItem, newItem)
    }

    final override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areContentsTheSame(oldItem, newItem)
    }

    final override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

