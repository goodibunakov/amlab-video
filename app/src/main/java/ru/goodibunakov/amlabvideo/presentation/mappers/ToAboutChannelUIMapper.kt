package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.AboutChannelEntity
import ru.goodibunakov.amlabvideo.presentation.model.AboutChannelUIModel

object ToAboutChannelUIMapper : Mapper<AboutChannelEntity, AboutChannelUIModel> {

    override fun map(from: AboutChannelEntity): AboutChannelUIModel {
        return AboutChannelUIModel(
            from.description,
            from.headerUrl
        )
    }
}