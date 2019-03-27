package com.ltei.lauviews.recycler

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup

class ListRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        layoutManager = LinearLayoutManager(context)
    }

    private var currentDivider: DividerItemDecoration? = null
    fun setupDivider(addDivider: Boolean = true) {
        if (addDivider) {
            if (currentDivider == null) {
                val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        divider.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.line_divider)!!)
                addItemDecoration(divider)
                currentDivider = divider
            }
        } else {
            currentDivider?.let {
                removeItemDecoration(it)
                currentDivider = null
            }
        }
    }

    class Adapter<T>(
        dataset: List<T>,
        private val viewHolderCreator: (parent: ViewGroup, type: Int) -> ObjectViewHolder<T>
    ) : RecyclerView.Adapter<ObjectViewHolder<T>>() {

        var dataset: List<T> = dataset
            private set

        constructor(viewHolderCreator: (parent: ViewGroup, type: Int) -> ObjectViewHolder<T>) :
                this(listOf(), viewHolderCreator)

        fun changeDataset(dataset: List<T>, notify: Boolean = true) {
            this.dataset = dataset
            if (notify) notifyDataSetChanged()
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = viewHolderCreator.invoke(p0, p1)
        override fun onBindViewHolder(holder: ObjectViewHolder<T>, position: Int) = holder.bind(dataset[position])
        override fun getItemCount() = dataset.size

    }

    class MutableAdapter<T>(
        dataset: MutableList<T>,
        private val viewHolderCreator: (parent: ViewGroup, type: Int) -> ObjectViewHolder<T>
    ) : RecyclerView.Adapter<ObjectViewHolder<T>>() {

        var dataset: MutableList<T> = dataset
            private set

        constructor(viewHolderCreator: (parent: ViewGroup, type: Int) -> ObjectViewHolder<T>) :
                this(mutableListOf(), viewHolderCreator)

        fun changeDataset(dataset: MutableList<T>, notify: Boolean = true) {
            this.dataset = dataset
            if (notify) notifyDataSetChanged()
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = viewHolderCreator.invoke(p0, p1)
        override fun onBindViewHolder(holder: ObjectViewHolder<T>, position: Int) = holder.bind(dataset[position])
        override fun getItemCount() = dataset.size

    }

}