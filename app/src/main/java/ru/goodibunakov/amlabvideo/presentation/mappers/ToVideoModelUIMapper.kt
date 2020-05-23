package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

object ToVideoModelUIMapper : Mapper<List<VideoEntity>, List<VideoUIModel>> {

    //TODO сколько прошло с даты публикации
    override fun map(from: List<VideoEntity>): List<VideoUIModel> {
        return from.map {
            VideoUIModel(
                    title = it.title,
                    videoId = it.videoId,
                    imageUrl = it.imageUrl,
                    createdDate = it.createdDate
            )
        }
    }
}