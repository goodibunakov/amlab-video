package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list.view.*
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

abstract class BaseViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    abstract fun bind(item: VideoUIModel?)
}

class VideoItemViewHolder(
        containerView: View,
        private val onClickListener: OnClickListener
) : BaseViewHolder(containerView) {

    override fun bind(item: VideoUIModel?) {
        item?.let {
            val requestOptions = RequestOptions()
                    .error(R.drawable.empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(containerView.context)
                    .load(item.imageUrl)
                    .apply(requestOptions)
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(containerView.imgThumbnail)

            containerView.txtTitle.text = item.title
            containerView.txtPublishedAt.text = item.createdDate

            containerView.star.setImageResource(if (it.star) R.drawable.star_filled else R.drawable.star_outline)
            containerView.star.setOnClickListener { onClickListener.onStarClick(item) }

            containerView.setOnClickListener { onClickListener.onItemClick(item) }
        }
    }
}

class LoadingViewHolder(containerView: View) : BaseViewHolder(containerView) {
    override fun bind(item: VideoUIModel?) {}
}