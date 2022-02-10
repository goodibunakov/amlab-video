package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.goodibunakov.amlabvideo.databinding.ItemListBinding
import ru.goodibunakov.amlabvideo.databinding.ItemListLoadingBinding
import ru.goodibunakov.amlabvideo.presentation.fragments.VideoFragment
import ru.goodibunakov.amlabvideo.presentation.interfaces.EmptyListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoAdapter(
    private val onClickListener: OnClickListener,
    private val emptyListener: EmptyListener
) : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    private val items: MutableList<VideoUIModel?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView =
                    ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VideoItemViewHolder(itemView, onClickListener)
            }
            else -> {
                val itemView = ItemListLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] != null) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    fun notifyItemChanged(videoId: String, fragmentType: VideoFragment.FragmentType) {
        Log.d("debug", "videoId = $videoId")
        val clickedItem = items.firstOrNull { it?.videoId == videoId }
        Log.d("debug", "clickedItem = $clickedItem")
        when (fragmentType) {
            VideoFragment.FragmentType.FROM_WEB -> {
                clickedItem?.let {
                    it.star = !it.star
                    notifyItemChanged(items.indexOf(clickedItem))
                }
            }
            VideoFragment.FragmentType.FROM_DB -> {
                val itemPosition = items.indexOf(clickedItem)
                items.remove(clickedItem)
                notifyItemRemoved(itemPosition)
                if (items.isEmpty()) emptyListener.listIsEmpty()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}