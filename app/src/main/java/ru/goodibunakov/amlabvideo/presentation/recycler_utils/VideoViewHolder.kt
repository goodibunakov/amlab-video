package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_list.view.*
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: VideoUIModel?)
}

class VideoItemViewHolder(
        itemView: View,
        private val onClickListener: OnClickListener
) : BaseViewHolder(itemView) {

    override fun bind(item: VideoUIModel?) {
        item?.let {
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

class LoadingViewHolder(itemView: View) : BaseViewHolder(itemView) {
    override fun bind(item: VideoUIModel?) {}
}