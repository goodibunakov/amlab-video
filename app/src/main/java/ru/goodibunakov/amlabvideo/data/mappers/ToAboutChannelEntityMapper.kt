package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.api.dto.channel_details.BrandingDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.AboutChannelEntity

object ToAboutChannelEntityMapper : Mapper<BrandingDTO, AboutChannelEntity> {

    override fun map(from: BrandingDTO): AboutChannelEntity {
        return AboutChannelEntity(
                from.items[0].brandingSettings.channel.description,
                from.items[0].brandingSettings.image.bannerMobileMediumHdImageUrl
        )
    }
}