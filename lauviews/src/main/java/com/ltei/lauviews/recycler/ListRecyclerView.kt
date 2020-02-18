package com.ltei.lauviews.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltei.lauviews.interfaces.IObjectViewBinder

abstract class ListRecyclerView<T> : RecyclerView, IObjectViewBinder<MutableList<T>> {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    val listAdapter = Adapter()
    override val objectView: View get() = this
    override val boundObject: MutableList<T>? get() = listAdapter.dataset

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = listAdapter
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

    override fun bind(obj: MutableList<T>) {
        listAdapter.changeDataset(obj)
    }

    protected abstract fun createViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder<T>

    inner class Adapter : RecyclerView.Adapter<ObjectViewHolder<T>>() {

        var dataset: MutableList<T> = mutableListOf()
            private set

        fun changeDataset(dataset: MutableList<T>, notify: Boolean = true) {
            this.dataset = dataset
            if (notify) notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder<T> {
            return this@ListRecyclerView.createViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: ObjectViewHolder<T>, position: Int) = holder.bind(dataset[position])
        override fun getItemCount() = dataset.size

    }


}