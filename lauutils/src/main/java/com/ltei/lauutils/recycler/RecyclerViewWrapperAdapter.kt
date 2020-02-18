package com.ltei.lauutils.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class RecyclerViewWrapperAdapter<T : RecyclerView.ViewHolder>(
    open val wrappedAdapter: RecyclerView.Adapter<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val headers = mutableListOf<(Context) -> View>()
    val footers = mutableListOf<(Context) -> View>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val wrappedAdapterPosition = position - headers.size
        if (wrappedAdapterPosition in 0 until wrappedAdapter.itemCount) {
            wrappedAdapter.onBindViewHolder(holder as T, wrappedAdapterPosition)
        }
    }

    private class ViewHolderImpl1(itemView: View) : RecyclerView.ViewHolder(itemView)
    private class ViewHolderImpl2(itemView: View) : RecyclerView.ViewHolder(itemView)
    private class ViewHolderImpl3(itemView: View) : RecyclerView.ViewHolder(itemView)
    private class ViewHolderImpl4(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_UNKNOWN) return ViewHolderImpl1(View(parent.context))

        if (isWrappedAdapterType(viewType)) {
            return wrappedAdapter.onCreateViewHolder(parent, viewType)
        }

        val headerIndex = viewType - VIEW_TYPE_HEADER_OR_FOOTER_0
        if (headerIndex in headers.indices) {
            val view = headers[headerIndex].invoke(parent.context)
            return ViewHolderImpl2(view)
        }

        val footerIndex =
            viewType - VIEW_TYPE_HEADER_OR_FOOTER_0 - headers.size - wrappedAdapter.itemCount
        if (footerIndex in footers.indices) {
            val view = footers[footerIndex].invoke(parent.context)
            return ViewHolderImpl3(view)
        }

        return ViewHolderImpl4(View(parent.context))
    }

    override fun getItemViewType(position: Int): Int = getItemViewType(
        wrappedAdapter = wrappedAdapter,
        headerCount = headers.size,
        dataCount = wrappedAdapter.itemCount,
        footerCount = footers.size,
        position = position
    )

    override fun getItemCount(): Int = headers.size + wrappedAdapter.itemCount + footers.size

    // Static

    companion object {
        const val VIEW_TYPE_UNKNOWN = -1024
        const val VIEW_TYPE_HEADER_OR_FOOTER_0 = 1024

        fun getItemViewType(
            wrappedAdapter: RecyclerView.Adapter<*>,
            headerCount: Int,
            dataCount: Int,
            footerCount: Int,
            position: Int
        ): Int {
            return when {
                position < 0 -> VIEW_TYPE_UNKNOWN
                position < headerCount -> VIEW_TYPE_HEADER_OR_FOOTER_0 + position
                position < headerCount + dataCount -> wrappedAdapter.getItemViewType(position - headerCount)
                position < headerCount + dataCount + footerCount -> VIEW_TYPE_HEADER_OR_FOOTER_0 + position
                else -> VIEW_TYPE_UNKNOWN
            }
        }

        fun isWrappedAdapterType(viewType: Int): Boolean {
            return viewType != VIEW_TYPE_UNKNOWN && viewType < VIEW_TYPE_HEADER_OR_FOOTER_0
        }
    }

    private class ViewHolderImpl(itemView: View) : RecyclerView.ViewHolder(itemView)
}