package ru.goodibunakov.amlabvideo.data.mappers

import android.util.Log
import ru.goodibunakov.amlabvideo.api.dto.videos_all.AllVideosDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

object ToVideoEntityMapperForAllVideosList : Mapper<AllVideosDTO, List<VideoEntity>> {
    override fun map(from: AllVideosDTO): List<VideoEntity> {
        Log.d("debug", "ToVideoEntityMapperForAllVideosList = $from")
        return from.items.map {
            Log.d("debug", "ToVideoEntityMapperForAllVideosList = $it")
            VideoEntity(
                    title = it.snippet.title,
                    videoId = it.id.videoId,
                    imageUrl = it.snippet.thumbnails.default.url,
                    createdDate = it.snippet.publishedAt
            )
        }
    }
}