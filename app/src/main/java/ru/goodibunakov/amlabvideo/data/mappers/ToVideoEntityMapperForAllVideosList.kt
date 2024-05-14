package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.api.dto.videos_all.AllVideosDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

object ToVideoEntityMapperForAllVideosList : Mapper<AllVideosDTO, List<VideoEntity>> {

    override fun map(from: AllVideosDTO): List<VideoEntity> {
        return from.items.map {
            VideoEntity(
                    title = it.snippet.title,
                    videoId = it.id.videoId,
                    playlistId = "",
                    imageUrl = it.snippet.thumbnails.default.url,
                    createdDate = it.snippet.publishedAt,
                    star = false
            )
        }
    }
}