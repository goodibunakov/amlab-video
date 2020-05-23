package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

object ToVideoEntityMapper : Mapper<VideoDTO, List<VideoEntity>> {

    override fun map(from: VideoDTO): List<VideoEntity> {
        return from.items.map {
            VideoEntity(
                    title = it.snippet.title,
                    videoId = it.snippet.resourceId.videoId,
                    imageUrl = it.snippet.thumbnails.standard.url,
                    createdDate = it.snippet.publishedAt
            )
        }
    }
}