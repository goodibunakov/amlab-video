package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class InfiniteScrollListener(private val linearLayoutManager: LinearLayoutManager,
                             private val listener: OnLoadMoreListener?)
    : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dx == 0 && dy == 0) return

        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        if (totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD && totalItemCount != 0) {
            listener?.onLoadMore()
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 2
    }
}