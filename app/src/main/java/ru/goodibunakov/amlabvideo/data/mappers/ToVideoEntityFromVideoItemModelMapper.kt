package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity

object ToVideoEntityFromVideoItemModelMapper : Mapper<List<VideoItemModel>, List<VideoEntity>> {

    override fun map(from: List<VideoItemModel>): List<VideoEntity> {
        return from.map {
            VideoEntity(
                    it.title,
                    it.videoId,
                    it.playlistId,
                    it.imageUrl,
                    it.createdDate,
                    it.star
            )
        }
    }
}