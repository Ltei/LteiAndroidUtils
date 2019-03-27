package com.ltei.lauviews.recycler

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

class RecyclerViewLoadMoreScrollListener(
    private val mLayoutManager: RecyclerView.LayoutManager,
    private val mOnLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var visibleThreshold = 5

    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0

    init {
        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> visibleThreshold *= mLayoutManager.spanCount
            is GridLayoutManager -> visibleThreshold *= mLayoutManager.spanCount
            is LinearLayoutManager -> {
            }
            else -> throw IllegalStateException()
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && isLastItemShowing()) {
            mOnLoadMore.invoke()
        }
    }

    fun isLastItemShowing(): Boolean {
        totalItemCount = mLayoutManager.itemCount

        lastVisibleItem = when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null)
                var maxSize = 0
                for (i in lastVisibleItemPositions.indices) {
                    if (i == 0) {
                        maxSize = lastVisibleItemPositions[i]
                    } else if (lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i]
                    }
                }
                maxSize
            }
            is GridLayoutManager -> mLayoutManager.findLastVisibleItemPosition()
            is LinearLayoutManager -> mLayoutManager.findLastVisibleItemPosition()
            else -> throw IllegalStateException()
        }

        return totalItemCount <= lastVisibleItem + visibleThreshold
    }

}