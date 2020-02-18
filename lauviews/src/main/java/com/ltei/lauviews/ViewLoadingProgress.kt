package com.ltei.lauviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import com.ltei.ljubase.Logger
import com.ltei.ljubase.interfaces.ILoadListener
import java.util.concurrent.atomic.AtomicInteger

class ViewLoadingProgress : ProgressBar, ILoadListener {

    private val loadingCount = AtomicInteger(0)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        visibility = View.GONE
    }

    fun clearLoads() {
        loadingCount.set(0)
    }

    override fun onStartLoad() {
        logger.info("onStartLoad")
        val count = loadingCount.incrementAndGet()
        if (count == 1) post { visibility = View.VISIBLE }
    }

    override fun onStopLoad() {
        logger.info("onStopLoad")
        val count = synchronized(loadingCount) {
            val count = loadingCount.decrementAndGet()
            if (count < 0) {
                loadingCount.set(0)
                0
            } else count
        }
        if (count == 0) {
            post { visibility = View.GONE }
        } else if (count < 0) {
            throw IllegalStateException()
        }
    }

    companion object {
        private val logger = Logger(ViewLoadingProgress::class.java)
    }

}