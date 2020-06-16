package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.api.dto.video_details.VideoDetailsDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.presentation.model.VideoDetailsUI
import java.text.SimpleDateFormat
import java.util.*

object ToVideoDetailsModelUIMapper : Mapper<VideoDetailsDTO, VideoDetailsUI> {

    override fun map(from: VideoDetailsDTO): VideoDetailsUI {
        val item = from.items[0]
        return VideoDetailsUI(
                viewCount = item.statistics.viewCount,
                likeCount = item.statistics.likeCount,
                commentCount = item.statistics.commentCount,
                favoriteCount = item.statistics.favoriteCount,
                dislikeCount = item.statistics.dislikeCount,
                title = item.snippet.title,
                description = item.snippet.description,
                publishedAt = item.snippet.publishedAt,
                publishedAtDate = convertToDate(item.snippet.publishedAt),
                tags = item.snippet.tags,
                categoryId = item.snippet.categoryId,
                liveBroadcastContent = item.snippet.liveBroadcastContent,
                duration = item.contentDetails.duration,
                thumbnail = item.snippet.thumbnails.default.url
        )
    }

    //2020-05-04T08:00:06Z
    private fun convertToDate(publishedAt: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(publishedAt) ?: Date()
    }
}