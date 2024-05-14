package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class InfiniteScrollListener(
    private val linearLayoutManager: LinearLayoutManager,
    private val listener: OnLoadMoreListener?
) : RecyclerView.OnScrollListener() {

    private var loading = false
    private var canLoadMore = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dx == 0 && dy == 0) return

        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        if (!loading && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD && totalItemCount != 0 && canLoadMore) {
            listener?.onLoadMore()
            loading = true
        }
    }

    fun setLoaded() {
        loading = false
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setCanLoadMore(canLoadMore: Boolean) {
        this.canLoadMore = canLoadMore
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 2
    }
}