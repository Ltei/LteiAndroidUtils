package com.ltei.lauutils.recycler

import androidx.recyclerview.widget.DiffUtil
import com.ltei.lauutils.ObjectViewHolder


class SimpleRecyclerViewWrapperAdapter<T>(
    override val wrappedAdapter: SimpleObjectViewAdapter<T>
) : RecyclerViewWrapperAdapter<ObjectViewHolder<T>>(wrappedAdapter) {

    fun setData(data: List<T>) {
        val formattedData = wrappedAdapter.formatData(data)
        val callback = DiffUtilCallback(wrappedAdapter.data, formattedData)
        val result = DiffUtil.calculateDiff(callback)
        wrappedAdapter.setData(formattedData, notify = false)
        result.dispatchUpdatesTo(this)
    }

    // Inner

    private inner class DiffUtilCallback(
        val oldData: List<T>,
        val newData: List<T>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = headers.size + oldData.size + footers.size
        override fun getNewListSize(): Int = headers.size + newData.size + footers.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItemType = getItemViewType(oldData, oldItemPosition)
            val newItemType = getItemViewType(newData, newItemPosition)

            if (isWrappedAdapterType(oldItemType) && isWrappedAdapterType(newItemType)) {
                val oldItem = oldData[oldItemPosition - headers.size]
                val newItem = newData[newItemPosition - headers.size]
                return wrappedAdapter.areItemsTheSame(oldItem, newItem)
            } else {
                run {
                    val oldHeaderIndex = getHeaderIndex(oldItemType)
                    val newHeaderIndex = getHeaderIndex(newItemType)
                    if (oldHeaderIndex in headers.indices && newHeaderIndex in headers.indices) {
                        return oldHeaderIndex == newHeaderIndex
                    }
                }
                run {
                    val oldFooterIndex = getFooterIndex(oldItemType, headers.size, oldData.size)
                    val newFooterIndex = getFooterIndex(newItemType, headers.size, newData.size)
                    if (oldFooterIndex in footers.indices && newFooterIndex in footers.indices) {
                        return oldFooterIndex == newFooterIndex
                    }
                }
            }

            return false // TODO Should be true
        }


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItemType = getItemViewType(oldData, oldItemPosition)
            val newItemType = getItemViewType(newData, newItemPosition)

            if (isWrappedAdapterType(oldItemType) && isWrappedAdapterType(newItemType)) {
                val oldItem = oldData[oldItemPosition - headers.size]
                val newItem = newData[newItemPosition - headers.size]
                return wrappedAdapter.areContentsTheSame(oldItem, newItem)
            } else {
                run {
                    val oldHeaderIndex = getHeaderIndex(oldItemType)
                    val newHeaderIndex = getHeaderIndex(newItemType)
                    if (oldHeaderIndex in headers.indices && newHeaderIndex in headers.indices) {
                        return oldHeaderIndex == newHeaderIndex
                    }
                }
                run {
                    val oldFooterIndex = getFooterIndex(oldItemType, headers.size, oldData.size)
                    val newFooterIndex = getFooterIndex(newItemType, headers.size, newData.size)
                    if (oldFooterIndex in footers.indices && newFooterIndex in footers.indices) {
                        return oldFooterIndex == newFooterIndex
                    }
                }
            }

            return false // TODO Should be true
        }

        private fun getItemViewType(data: List<T>, position: Int): Int = getItemViewType(
            wrappedAdapter = wrappedAdapter,
            headerCount = headers.size,
            dataCount = data.size,
            footerCount = footers.size,
            position = position
        )
    }

}