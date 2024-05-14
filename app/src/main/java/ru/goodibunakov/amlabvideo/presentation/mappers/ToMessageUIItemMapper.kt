package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.MessageEntity
import ru.goodibunakov.amlabvideo.presentation.model.MessageUIItem

object ToMessageUIItemMapper : Mapper<MessageEntity, MessageUIItem> {

    override fun map(from: MessageEntity): MessageUIItem {
        return MessageUIItem(
                id = from.id,
                title = from.title,
                body = from.body,
                image = from.image,
                launchURL = from.launchURL,
                dateReceived = from.dateReceived
        )
    }
}