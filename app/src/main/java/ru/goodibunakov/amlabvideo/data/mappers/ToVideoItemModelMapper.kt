package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.domain.Mapper

object ToVideoItemModelMapper : Mapper<VideoDTO, List<VideoItemModel>> {

    override fun map(from: VideoDTO): List<VideoItemModel> {
        return from.items.map {
            VideoItemModel(
                    title = it.snippet.title,
                    videoId = it.snippet.resourceId.videoId,
                    playlistId = it.snippet.playlistId,
                    imageUrl = it.snippet.thumbnails?.default?.url ?: "",
                    createdDate = it.snippet.publishedAt,
                    star = false
            )
        }
    }
}