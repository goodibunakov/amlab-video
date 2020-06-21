package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.MessageEntity

object ToMessageEntityMapper: Mapper<MessageItem, MessageEntity> {

    override fun map(from: MessageItem): MessageEntity {
        return MessageEntity(
                id = from.id,
                title = from.title,
                body = from.body,
                image = from.image,
                launchURL = from.launchURL ?: "",
                dateReceived = from.dateReceived
        )
    }
}