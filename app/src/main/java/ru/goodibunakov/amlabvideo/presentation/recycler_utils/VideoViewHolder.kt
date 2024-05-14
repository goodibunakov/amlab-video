package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.databinding.ItemListBinding
import ru.goodibunakov.amlabvideo.databinding.ItemListLoadingBinding
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

abstract class BaseViewHolder<T : ViewBinding>(binding: T) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: VideoUIModel?)
}

class VideoItemViewHolder(
    private val binding: ItemListBinding,
    private val onClickListener: OnClickListener
) : BaseViewHolder<ViewBinding>(binding) {

    override fun bind(item: VideoUIModel?) {
        item?.let {
            val requestOptions = RequestOptions()
                .error(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .apply(requestOptions)
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions().crossFade())
                .into(binding.imgThumbnail)

            with(binding) {
                txtTitle.text = item.title
                txtPublishedAt.text = item.createdDate

                star.setImageResource(if (it.star) R.drawable.star_filled else R.drawable.star_outline)
                star.setOnClickListener { onClickListener.onStarClick(item) }

                root.setOnClickListener { onClickListener.onItemClick(item) }
            }
        }
    }
}

class LoadingViewHolder(binding: ItemListLoadingBinding) : BaseViewHolder<ViewBinding>(binding) {
    override fun bind(item: VideoUIModel?) {}
}