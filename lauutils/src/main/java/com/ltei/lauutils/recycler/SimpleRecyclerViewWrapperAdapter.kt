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
        wrappedAdapter.setData(formattedData)
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

            return when {
                isWrappedAdapterType(oldItemType) && isWrappedAdapterType(newItemType) -> {
                    val oldItem = oldData[oldItemPosition - headers.size]
                    val newItem = newData[newItemPosition - headers.size]
                    wrappedAdapter.areItemsTheSame(oldItem, newItem)
                }
                else -> oldItemType == newItemType
            }
        }


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItemType = getItemViewType(oldData, oldItemPosition)
            val newItemType = getItemViewType(newData, newItemPosition)

            return when {
                isWrappedAdapterType(oldItemType) && isWrappedAdapterType(newItemType) -> {
                    val oldItem = oldData[oldItemPosition - headers.size]
                    val newItem = newData[newItemPosition - headers.size]
                    wrappedAdapter.areContentsTheSame(oldItem, newItem)
                }
                else -> oldItemType == newItemType
            }
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