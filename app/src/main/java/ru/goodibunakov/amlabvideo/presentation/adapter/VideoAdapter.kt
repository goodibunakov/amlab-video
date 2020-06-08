package ru.goodibunakov.amlabvideo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_list.view.*
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

class VideoAdapter(private val onClickListener: OnClickListener) : RecyclerView.Adapter<VideoAdapter.VideoItemViewHolder>() {

    private val items: MutableList<VideoUIModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return VideoItemViewHolder(itemView, onClickListener)
    }

    fun setItems(list: List<VideoUIModel>) {
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
        holder.bind(items[position])
    }


    inner class VideoItemViewHolder(itemView: View, private val onClickListener: OnClickListener) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: VideoUIModel) {
            val requestOptions = RequestOptions()
                    .error(R.drawable.empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .apply(requestOptions)
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(itemView.imgThumbnail)

            itemView.txtTitle.text = item.title
            itemView.txtPublishedAt.text = item.createdDate

            itemView.setOnClickListener { onClickListener.onItemClick(item) }
        }
    }
}