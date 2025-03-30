package com.bsvillarraga.spaceflightnews.core.extensions

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.init(adapter: RecyclerView.Adapter<*>, context: Context) {
    layoutManager = LinearLayoutManager(context)
    this.adapter = adapter
}

fun RecyclerView.onLoadMoreScrollListener(loadMore: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!recyclerView.canScrollVertically(1)) {
                loadMore.invoke()
            }
        }
    })
}