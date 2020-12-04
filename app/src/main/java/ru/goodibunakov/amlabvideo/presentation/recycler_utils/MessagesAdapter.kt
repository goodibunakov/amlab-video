package ru.goodibunakov.amlabvideo.presentation.recycler_utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.perfomer.blitz.setTimeAgo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_message.view.*
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.model.MessageUIItem

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    private var messageList = mutableListOf<MessageUIItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessagesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    fun addItems(list: List<MessageUIItem>) {
        Log.d("debug", "MessagesAdapter addItems = $list")
        if (list.isNotEmpty()) {
            val newItems = list.dropLast(messageList.size)
            messageList.addAll(0, newItems)
            notifyItemRangeInserted(0, newItems.size)
        } else {
            messageList.clear()
            notifyItemRangeRemoved(0, itemCount)
        }
    }

    inner class MessagesViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: MessageUIItem?) {
            item?.let {
                if (item.image.isNotEmpty()) {
                    containerView.messageImage.visibility = View.VISIBLE
                    val requestOptions = RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                    Glide.with(containerView.context)
                        .load(item.image)
                        .apply(requestOptions)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.empty_photo)
                        .error(R.drawable.empty_photo)
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(containerView.messageImage)
                } else {
                    containerView.messageImage.visibility = View.GONE
                }

                containerView.messageTitle.text = item.title
                containerView.messageBody.text = item.body
                containerView.messageDate.setTimeAgo(time = item.dateReceived, showSeconds = false, autoUpdate = false)
            }
        }
    }
}