package com.ltei.lauverticaldrawer


import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ListView
import android.widget.ScrollView


class ScrollableViewHelper {

    /**
     * Returns the current scroll position of the scrollable view. If this method returns zero or
     * less, it means at the scrollable view is in a position such as the panel should handle
     * scrolling. If the method returns anything above zero, then the panel will let the scrollable
     * view handle the scrolling
     *
     * @param scrollableView the scrollable view
     * @param isSlidingUp whether or not the panel is sliding up or down
     * @return the scroll position
     */
    fun getScrollableViewScrollPosition(scrollableView: View?, isSlidingUp: Boolean): Int {
        if (scrollableView == null) return 0
        if (scrollableView is ScrollView) {
            return if (isSlidingUp) {
                scrollableView.scrollY
            } else {
                val child = scrollableView.getChildAt(0)
                child.bottom - (scrollableView.getHeight() + scrollableView.getScrollY())
            }
        } else if (scrollableView is ListView && scrollableView.childCount > 0) {
            if (scrollableView.adapter == null) return 0
            return if (isSlidingUp) {
                val firstChild = scrollableView.getChildAt(0)
                // Approximate the scroll position based on the top child and the first visible item
                scrollableView.firstVisiblePosition * firstChild.height - firstChild.top
            } else {
                val lastChild = scrollableView.getChildAt(scrollableView.childCount - 1)
                // Approximate the scroll position based on the bottom child and the last visible item
                (scrollableView.adapter.count - scrollableView.lastVisiblePosition - 1) * lastChild.height + lastChild.bottom - scrollableView.getBottom()
            }
        } else if (scrollableView is RecyclerView && scrollableView.childCount > 0) {
            val layoutManager = scrollableView.layoutManager!!
            if (scrollableView.adapter == null) return 0
            return if (isSlidingUp) {
                val firstChild = scrollableView.getChildAt(0)
                // Approximate the scroll position based on the top child and the first visible item
                scrollableView.getChildLayoutPosition(firstChild) * layoutManager.getDecoratedMeasuredHeight(firstChild) - layoutManager.getDecoratedTop(firstChild)
            } else {
                val lastChild = scrollableView.getChildAt(scrollableView.childCount - 1)
                // Approximate the scroll position based on the bottom child and the last visible item
                (scrollableView.adapter!!.itemCount - 1) * layoutManager.getDecoratedMeasuredHeight(lastChild) + layoutManager.getDecoratedBottom(lastChild) - scrollableView.getBottom()
            }
        } else {
            return 0
        }
    }
}
