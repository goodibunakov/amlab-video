package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoAdapter(private val onClickListener: OnClickListener) : RecyclerView.Adapter<BaseViewHolder>() {

    private val items: MutableList<VideoUIModel?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
                VideoItemViewHolder(itemView, onClickListener)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_loading, parent, false)
                LoadingViewHolder(itemView)
            }
        }
    }

    fun addItems(list: List<VideoUIModel>) {
        Log.d("ddd", "size1 = ${items.size}")
        items.addAll(list)
        Log.d("ddd", "size2 = ${items.size}")
        notifyItemRangeInserted(itemCount, list.size)
    }

    fun addNull() {
        items.add(null)
        notifyItemInserted(items.size - 1)
        Log.d("ddd", "addNull = ${items.size}")
    }

    fun removeNull() {
        items.removeAt(items.size - 1)
        notifyItemRemoved(items.size)
        Log.d("ddd", "removeNull = ${items.size}")
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] != null) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}